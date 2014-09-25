package org.jboss.jdf.example.ticketmonster.command;

import org.tomitribe.crest.api.Command;
import org.tomitribe.crest.api.StreamingOutput;
import org.tomitribe.crest.connector.api.CrestListener;
import org.tomitribe.crest.connector.api.TerminalSessionScoped;

import javax.ejb.MessageDriven;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Properties;
import java.util.TreeSet;

@Interceptors({ CountInterceptor.class })
@MessageDriven(name = "SystemInfoBean")
public class SystemInfoBean implements CrestListener {

    @Inject
    @Pwd
    private Path path;

    @Command
    public String pwd() {
        final File file = path.get();
        return file.getAbsolutePath();
    }
}
