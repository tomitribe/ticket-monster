package org.jboss.jdf.example.ticketmonster.command;

import org.tomitribe.crest.api.Command;
import org.tomitribe.crest.connector.api.CrestListener;

import javax.ejb.MessageDriven;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import java.io.File;

@Interceptors({CountInterceptor.class})
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
