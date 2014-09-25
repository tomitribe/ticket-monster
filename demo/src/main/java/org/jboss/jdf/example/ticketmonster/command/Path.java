/* =====================================================================
 *
 * Copyright (c) 2011 David Blevins.  All rights reserved.
 *
 * =====================================================================
 */
package org.jboss.jdf.example.ticketmonster.command;

import java.io.File;

public class Path {
    private File file;

    public Path() {
    }

    public Path(File file) {
        this.file = file;
    }

    public File get() {
        return file;
    }
}
