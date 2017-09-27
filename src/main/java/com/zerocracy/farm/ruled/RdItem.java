/**
 * Copyright (c) 2016-2017 Zerocracy
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
package com.zerocracy.farm.ruled;

import com.zerocracy.jstk.Item;
import com.zerocracy.jstk.Project;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicReference;
import lombok.EqualsAndHashCode;

/**
 * Ruled item.
 *
 * @author Yegor Bugayenko (yegor256@gmail.com)
 * @version $Id$
 * @since 0.17
 * @checkstyle ClassDataAbstractionCouplingCheck (500 lines)
 */
@EqualsAndHashCode(of = "origin")
final class RdItem implements Item {

    /**
     * Original project.
     */
    private final Project project;

    /**
     * Original item.
     */
    private final Item origin;

    /**
     * The location of the file.
     */
    private final AtomicReference<Path> file;

    /**
     * The length of the file.
     */
    private long length;

    /**
     * Ctor.
     * @param pkt Project
     * @param item Original item
     */
    RdItem(final Project pkt, final Item item) {
        this.project = pkt;
        this.origin = item;
        this.file = new AtomicReference<>();
        this.length = 0L;
    }

    @Override
    public String toString() {
        return this.origin.toString();
    }

    @Override
    public Path path() throws IOException {
        final Path path = this.origin.path();
        this.file.set(path);
        if (Files.exists(path) && path.toFile().length() > 0L) {
            this.length = path.toFile().length();
        }
        return path;
    }

    @Override
    public void close() throws IOException {
        final Path path = this.file.get();
        final boolean modified = path != null
            && Files.exists(path)
            && this.length != path.toFile().length();
        if (modified) {
            new RdAuto(this.project, path).propagate();
            new RdRules(this.project, path).validate();
        }
        this.origin.close();
    }

}
