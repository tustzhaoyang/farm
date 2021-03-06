/**
 * Copyright (c) 2016-2018 Zerocracy
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to read
 * the Software only. Permissions is hereby NOT GRANTED to use, copy, modify,
 * merge, publish, distribute, sublicense, and/or sell copies of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.zerocracy.stk.pm.cost

import com.jcabi.xml.XML
import com.zerocracy.Farm
import com.zerocracy.Par
import com.zerocracy.Policy
import com.zerocracy.Project
import com.zerocracy.cash.Cash
import com.zerocracy.farm.Assume
import com.zerocracy.pm.ClaimIn
import com.zerocracy.pm.cost.Rates
import com.zerocracy.pmo.Debts
import com.zerocracy.pmo.People
import com.zerocracy.pmo.banks.Payroll

def exec(Project project, XML xml) {
  new Assume(project, xml).notPmo()
  new Assume(project, xml).type('Make payment')
  ClaimIn claim = new ClaimIn(xml)
  String job = claim.param('job')
  String login = claim.param('login')
  String reason = claim.param('reason')
  int minutes = Integer.parseInt(claim.param('minutes'))
  if (minutes < 0) {
    return
  }
  if (login == 'yegor256') {
    return
  }
  Cash price
  if (claim.hasParam('cash')) {
    price = new Cash.S(claim.param('cash'))
  } else {
    Cash rate = Cash.ZERO
    Rates rates = new Rates(project).bootstrap()
    if (rates.exists(login)) {
      rate = rates.rate(login)
    }
    price = rate.mul(minutes) / 60
  }
  if (price != Cash.ZERO) {
    String tail = ''
    Farm farm = binding.variables.farm
    People people = new People(farm).bootstrap()
    if (!claim.hasParam('no-tuition-fee') && people.hasMentor(login) && people.mentor(login) != '0crat') {
      int share = new Policy().get('45.share', 8)
      Cash fee = price.mul(share) / 100
      price = price.mul(100 - share) / 100
      String mentor = people.mentor(login)
      claim.copy()
        .type('Make payment')
        .param('login', mentor)
        .param('minutes', 0)
        .param('cash', fee)
        .param('student', login)
        .param('no-tuition-fee', true)
        .param('message', new Par('Tuition fee from @%s').say(login))
        .postTo(project)
      tail = new Par(
        'the tuition fee %s was deducted',
        'and sent to @%s (your mentor), according to §45'
      ).say(fee, mentor)
    }
    String msg
    try {
      msg = new Payroll(farm).pay(
        project, login, price,
        "Payment for ${job} (${minutes} minutes): ${reason}"
      )
      claim.copy()
        .type('Notify user')
        .token("user;${login}")
        .param(
          'message',
          new Par(
            'We just paid you %s (`%s`) for %s: %s'
          ).say(price, msg, job, reason) + tail
        )
        .postTo(project)
    } catch (IOException ex) {
      Debts debts = new Debts(farm).bootstrap()
      debts.add(login, price, "${reason} at ${job}", ex.message)
      claim.copy()
        .type('Notify user')
        .token("user;${login}")
        .param(
          'message',
          new Par(
            'We are very sorry, but we failed to pay you %s for %s: "%s";',
            'this amount was added to the list of payments we owe you;',
            'we will try to send them all together very soon;',
            'we will keep you informed, see §20'
          ).say(price, job, ex.message) + tail
        )
        .postTo(project)
    }
    claim.copy()
      .type('Notify project')
      .param(
        'message',
        new Par(
          'We just paid %s to @%s for %s: %s'
        ).say(price, login, job, reason)
      )
      .postTo(project)
  }
}
