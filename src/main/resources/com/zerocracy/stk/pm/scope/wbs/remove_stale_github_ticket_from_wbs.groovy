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
package com.zerocracy.stk.pm.scope.wbs

import com.jcabi.github.Github
import com.jcabi.github.Issue
import com.jcabi.xml.XML
import com.zerocracy.Farm
import com.zerocracy.Project
import com.zerocracy.entry.ExtGithub
import com.zerocracy.farm.Assume
import com.zerocracy.pm.scope.Wbs
import com.zerocracy.radars.github.Job
import com.zerocracy.radars.github.Quota

def exec(Project project, XML xml) {
  new Assume(project, xml).notPmo()
  new Assume(project, xml).type('Ping daily')
  Farm farm = binding.variables.farm
  Github github = new ExtGithub(farm).value()
  Wbs wbs = new Wbs(project).bootstrap()
//  Date threshold = java.sql.Date.valueOf(LocalDate.now().minusDays(5))
  int done = 0
  for (String job : wbs.iterate()) {
    if (!new Quota(github).quiet()) {
      return
    }
    Issue.Smart issue = new Issue.Smart(new Job.Issue(github, job))
    if (issue.open) {
      continue
    }
    // @todo #362:30min This verification is disabled because GitHub
    //  goes over quota very soon, since some issues have too many events.
    //  Let's find a way to make less requests to GitHub. The same problem
    //  exists in finish_stale_github_order.groovy
//    Date closed = new Event.Smart(issue.latestEvent(Event.CLOSED)).createdAt()
//    if (closed > threshold) {
//      continue
//    }
    claim.copy()
      .type('Remove job from WBS')
      .token("job;${job}")
      .param('job', job)
      .param('reason', 'GitHub issue is already closed')
      .postTo(project)
    if (++done > 10) {
      break
    }
  }
}
