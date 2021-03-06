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
package com.zerocracy.pm.in;

import com.zerocracy.Project;
import com.zerocracy.cash.Cash;
import com.zerocracy.farm.fake.FkProject;
import com.zerocracy.pm.cost.Estimates;
import com.zerocracy.pm.cost.Ledger;
import com.zerocracy.pm.cost.Rates;
import com.zerocracy.pm.scope.Wbs;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

/**
 * Test case for {@link Orders}.
 * @author Yegor Bugayenko (yegor256@gmail.com)
 * @version $Id$
 * @since 0.10
 * @checkstyle JavadocMethodCheck (500 lines)
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 */
public final class OrdersTest {

    @Test
    public void assignsAndResigns() throws Exception {
        final Project project = new FkProject();
        final Orders orders = new Orders(project).bootstrap();
        final String job = "gh:yegor256/0pdd#13";
        new Wbs(project).bootstrap().add(job);
        orders.assign(job, "yegor256", "just for fun");
    }

    @Test
    public void setsEstimatesOnAssign() throws Exception {
        final Project project = new FkProject();
        new Ledger(project).bootstrap().add(
            new Ledger.Transaction(
                new Cash.S("$1000"),
                "assets", "cash",
                "income", "sponsor",
                "There is some funding just arrived"
            )
        );
        final String login = "dmarkov";
        new Rates(project).bootstrap().set(login, new Cash.S("$50"));
        final String job = "gh:yegor256/0pdd#19";
        final Wbs wbs = new Wbs(project).bootstrap();
        wbs.add(job);
        wbs.role(job, "REV");
        final Orders orders = new Orders(project).bootstrap();
        orders.assign(job, login, "just for fun again");
        MatcherAssert.assertThat(
            new Estimates(project).bootstrap().get(job),
            Matchers.equalTo(new Cash.S("$12.50"))
        );
    }
}
