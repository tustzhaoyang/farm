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

import com.zerocracy.Item;
import com.zerocracy.Project;
import com.zerocracy.SoftException;
import com.zerocracy.Txn;
import com.zerocracy.Xocument;
import com.zerocracy.pm.cost.Boosts;
import com.zerocracy.pm.scope.Wbs;
import java.io.IOException;
import java.time.LocalDate;
import java.time.chrono.ChronoZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Date;
import org.cactoos.list.SolidList;
import org.cactoos.text.JoinedText;
import org.cactoos.time.DateAsText;
import org.cactoos.time.DateOf;
import org.xembly.Directives;

/**
 * Work orders.
 *
 * @author Yegor Bugayenko (yegor256@gmail.com)
 * @version $Id$
 * @since 0.10
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 */
@SuppressWarnings("PMD.TooManyMethods")
public final class Orders {

    /**
     * Project.
     */
    private final Project project;

    /**
     * Ctor.
     * @param pkt Project
     */
    public Orders(final Project pkt) {
        this.project = pkt;
    }

    /**
     * Bootstrap it.
     * @return Itself
     * @throws IOException If fails
     */
    public Orders bootstrap() throws IOException {
        try (final Item wbs = this.item()) {
            new Xocument(wbs.path()).bootstrap("pm/in/orders");
        }
        return this;
    }

    /**
     * Assign job to performer.
     * @param job The job to assign
     * @param login The login of the user
     * @param reason The reason of this order
     * @throws IOException If fails
     */
    public void assign(final String job, final String login,
        final String reason) throws IOException {
        if (this.assigned(job)) {
            throw new SoftException(
                String.format(
                    "Job `%s` already assigned to @%s, can't assign to @%s",
                    job, this.performer(job), login
                )
            );
        }
        if (!new Wbs(this.project).bootstrap().exists(job)) {
            throw new SoftException(
                String.format(
                    "Job `%s` doesn't exist in WBS, can't create order",
                    job
                )
            );
        }
        try (final Txn txn = new Txn(this.project)) {
            try (final Item wbs = Orders.item(this.project)) {
                new Xocument(wbs.path()).modify(
                    new Directives()
                        .xpath(
                            String.format(
                                "/orders[not(order[@job='%s'])]",
                                job
                            )
                        )
                        .strict(1)
                        .add("order")
                        .attr("job", job)
                        .add("created").set(new DateAsText().asString()).up()
                        .add("performer")
                        .set(login)
                        .up()
                        .add("reason")
                        .set(reason)
                );
            }
            final String role = new Wbs(this.project).bootstrap().role(job);
            int factor = 2;
            if ("REV".equals(role)) {
                factor = 1;
            }
            new Boosts(this.project).bootstrap().boost(job, factor);
            txn.commit();
        }
    }

    /**
     * Resign current job performer.
     * @param job The job to touch
     * @throws IOException If fails
     */
    public void resign(final String job) throws IOException {
        if (!this.assigned(job)) {
            throw new SoftException(
                String.format(
                    "Job `%s` is not assigned to anyone",
                    job
                )
            );
        }
        try (final Item wbs = this.item()) {
            new Xocument(wbs.path()).modify(
                new Directives()
                    .xpath(String.format("/orders/order[@job ='%s']", job))
                    .strict(1)
                    .remove()
            );
        }
    }

    /**
     * All jobs we have orders for.
     * @return List of jobs
     * @throws IOException If fails of it there is no assignee
     */
    public Collection<String> iterate() throws IOException {
        try (final Item wbs = this.item()) {
            return new Xocument(wbs.path()).xpath(
                "/orders/order/@job  "
            );
        }
    }

    /**
     * Job is assigned.
     * @param job The job
     * @return TRUE if assigned
     * @throws IOException If fails of it there is no assignee
     */
    public boolean assigned(final String job) throws IOException {
        try (final Item wbs = this.item()) {
            return !new Xocument(wbs.path()).nodes(
                String.format("/orders/order[@job='%s']", job)
            ).isEmpty();
        }
    }

    /**
     * Get job assignee.
     * @param job The job
     * @return Performer GitHub login
     * @throws IOException If fails of it there is no assignee
     */
    public String performer(final String job) throws IOException {
        if (!this.assigned(job)) {
            throw new SoftException(
                String.format(
                    "Job `%s` is not assigned, can't get performer", job
                )
            );
        }
        try (final Item wbs = this.item()) {
            return new Xocument(wbs.path()).xpath(
                String.format("/orders/order[@job='%s']/performer/text()", job)
            ).get(0);
        }
    }

    /**
     * Start time of order.
     * @param job Order's job
     * @return Start DateTime
     * @throws IOException If fails
     */
    public Date startTime(final String job) throws IOException {
        if (!this.assigned(job)) {
            throw new SoftException(
                String.format(
                    "Job `%s` is not assigned, can't get start time", job
                )
            );
        }
        try (final Item orders = this.item()) {
            return new DateOf(
                new Xocument(orders.path()).xpath(
                    String.format(
                        "/orders/order[@job='%s']/created/text()",
                        job
                    )
                ).get(0)
            ).value();
        }
    }

    /**
     * Orders which were created older than specified time.
     * @param time Time to compare
     * @return Job list
     * @throws IOException If fails
     */
    public Iterable<String> olderThan(final ChronoZonedDateTime<LocalDate> time)
        throws IOException {
        try (final Item item = this.item()) {
            return new SolidList<>(
                new Xocument(item.path()).xpath(
                    new JoinedText(
                        "",
                        "/orders/order[",
                        "xs:dateTime(created) < xs:dateTime('",
                        time.format(DateTimeFormatter.ISO_INSTANT),
                        "')]/@job"
                    ).asString()
                )
            );
        }
    }

    /**
     * List of jobs of given person.
     * @param login Performer
     * @return List of job IDs
     * @throws IOException If fails
     */
    public Iterable<String> jobs(final String login) throws IOException {
        try (final Item item = this.item()) {
            return new Xocument(item.path()).xpath(
                String.format("/orders/order[performer='%s']/@job", login)
            );
        }
    }

    /**
     * The item.
     * @return Item
     * @throws IOException If fails
     */
    private Item item() throws IOException {
        return Orders.item(this.project);
    }

    /**
     * Item in project.
     * @param pkt A project
     * @return Item
     * @throws IOException If fails
     */
    private static Item item(final Project pkt) throws IOException {
        return pkt.acq("orders.xml");
    }
}
