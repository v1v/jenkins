/*
 * The MIT License
 *
 * Copyright (c) 2004-2009, Sun Microsystems, Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package hudson.model;

import jenkins.model.Jenkins;
import org.jvnet.localizer.Localizable;
import org.kohsuke.stapler.export.Exported;
import org.kohsuke.stapler.export.ExportedBean;

import javax.annotation.Nonnull;
import java.util.Date;

/**
 * Represents a cause that puts a {@linkplain AbstractProject#isDisabled() project disable}.
 *
 * <h2>Views</h2>
 * <p>
 * {@link DisableCause} must have <tt>cause.jelly</tt> that renders a cause
 * into HTML. This is used to tell users why the node is put offline.
 * This view should render a block element like DIV.
 *
 * @author Victor Martinez
 * @since TODO:
 */
@ExportedBean
public abstract class DisableCause {
    protected final long timestamp = System.currentTimeMillis();

    /**
     * Timestamp in which the event happened.
     *
     * @since TODO:
     */
    @Exported
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * Same as {@link #getTimestamp()} but in a different type.
     *
     * @since TODO:
     */
    public final @Nonnull Date getTime() {
        return new Date(timestamp);
    }

    /**
     * {@link DisableCause} that renders a static text,
     * but without any further UI.
     */
    public static class SimpleDisableCause extends DisableCause {
        public final Localizable description;

        /**
         * @since TODO:
         */
        protected SimpleDisableCause(Localizable description) {
            this.description = description;
        }

        @Exported(name="description") @Override
        public String toString() {
            return description.toString();
        }
    }

    public static DisableCause create(Localizable d) {
        if (d==null)    return null;
        return new SimpleDisableCause(d);
    }

    /**
     * Taken offline by user.
     * @since TODO
     */
    public static class UserCause extends SimpleDisableCause {
        private final User user;

        public UserCause(User user, String message) {

            super(Messages._AbstractProject_DisabledBy(
                    user != null ? user.getId() : Jenkins.ANONYMOUS.getName(),
                    message != null ? " : " + message : ""
            ));
            this.user = user;
        }

        public User getUser() {
            return user;
        }
    }

    public static class ByCLI extends UserCause {
        @Exported
        public final String message;

        public ByCLI(String message) {
            super(User.current(), message);
            this.message = message;
        }
    }
}
