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
package com.zerocracy.stk.pm.in.orders

import com.jcabi.github.Github
import com.jcabi.github.Issue
import com.jcabi.xml.XML
import com.zerocracy.Farm
import com.zerocracy.Project
import com.zerocracy.entry.ExtGithub
import com.zerocracy.farm.Assume
import com.zerocracy.pm.in.Orders
import com.zerocracy.radars.github.Job
import com.zerocracy.radars.github.Quota

def exec(Project project, XML xml) {
  new Assume(project, xml).notPmo()
  new Assume(project, xml).type('Ping daily')
  Farm farm = binding.variables.farm
  Github github = new ExtGithub(farm).value()
  Orders orders = new Orders(project).bootstrap()
//  Date threshold = java.sql.Date.valueOf(LocalDate.now().minusDays(5))
  int done = 0
  for (String job : orders.iterate()) {
    if (!new Quota(github).quiet()) {
      return
    }
    Issue.Smart issue = new Issue.Smart(new Job.Issue(github, job))
    if (issue.open) {
      continue
    }
//    Date closed = new Event.Smart(issue.latestEvent(Event.CLOSED)).createdAt()
//    if (closed > threshold) {
//      continue
//    }
    claim.copy()
      .type('Finish order')
      .param('job', job)
      .param('reason', 'GitHub issue is already closed')
      .postTo(project)
    if (++done > 10) {
      break
    }
  }
}
