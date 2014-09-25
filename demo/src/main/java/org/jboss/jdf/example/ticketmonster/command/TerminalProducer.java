/* =====================================================================
 *
 * Copyright (c) 2011 David Blevins.  All rights reserved.
 *
 * =====================================================================
 */
package org.jboss.jdf.example.ticketmonster.command;


import org.tomitribe.crest.connector.api.TerminalSessionScoped;

import javax.enterprise.inject.Produces;
import java.io.File;

public class TerminalProducer {

    @Pwd
    @Produces
    @TerminalSessionScoped
    public Path currentDir() {
        return new Path(new File(System.getProperty("user.dir")));
    }

}
