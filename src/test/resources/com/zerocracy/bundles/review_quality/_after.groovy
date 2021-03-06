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
package com.zerocracy.bundles.review_quality

import com.jcabi.xml.XML
import com.zerocracy.Farm
import com.zerocracy.Project
import com.zerocracy.pm.in.Orders
import com.zerocracy.pmo.Awards
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers

def exec(Project project, XML xml) {
  Orders orders = new Orders(project).bootstrap()
  MatcherAssert.assertThat(
    'job 1 still exists',
    orders.assigned('gh:test/test#1'),
    Matchers.is(false)
  )
  MatcherAssert.assertThat(
    'job 2 still exists',
    orders.assigned('gh:test/test#2'),
    Matchers.is(false)
  )
  MatcherAssert.assertThat(
    'job 3 still exists',
    orders.assigned('gh:test/test#3'),
    Matchers.is(false)
  )
  Farm farm = binding.variables.farm
  MatcherAssert.assertThat(
    'qauser received incorrect awards',
    new Awards(farm, 'qauser').bootstrap().total(),
    Matchers.equalTo(24)
  )
}
