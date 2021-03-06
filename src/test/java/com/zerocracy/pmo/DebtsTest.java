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
package com.zerocracy.pmo;

import com.jcabi.matchers.XhtmlMatchers;
import com.zerocracy.Par;
import com.zerocracy.cash.Cash;
import com.zerocracy.farm.fake.FkProject;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.xembly.Xembler;

/**
 * Test case for {@link Debts}.
 * @author Yegor Bugayenko (yegor256@gmail.com)
 * @version $Id$
 * @since 0.21
 * @checkstyle JavadocMethodCheck (500 lines)
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 */
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public final class DebtsTest {

    @Test
    public void addsAndRemoves() throws Exception {
        final Debts debts = new Debts(new FkProject()).bootstrap();
        final String uid = "yegor256";
        debts.add(uid, new Cash.S("$5"), "details 1", "reason 1");
        debts.add(uid, new Cash.S("$6"), "details 2", "reason 2");
        MatcherAssert.assertThat(
            debts.amount(uid),
            Matchers.equalTo(new Cash.S("$11"))
        );
    }

    @Test
    public void printsSingleToXembly() throws Exception {
        final Debts debts = new Debts(new FkProject()).bootstrap();
        final String uid = "0crat";
        debts.add(
            uid, new Cash.S("$99"),
            new Par("details-1 as in §1").say(),
            "reason-1"
        );
        debts.add(uid, new Cash.S("$17"), "details-15", "reason-15");
        MatcherAssert.assertThat(
            XhtmlMatchers.xhtml(
                new Xembler(debts.toXembly(uid)).xmlQuietly()
            ),
            XhtmlMatchers.hasXPaths(
                "/debt[@total='$116.00']",
                "/debt[count(item)=2]",
                "/debt/item[details='details-1 as in §1 (reason-1)']"
            )
        );
    }

}
