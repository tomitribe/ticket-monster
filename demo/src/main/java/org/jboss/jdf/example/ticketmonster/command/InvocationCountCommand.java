package org.jboss.jdf.example.ticketmonster.command;

import org.tomitribe.crest.api.Command;
import org.tomitribe.crest.connector.api.CrestListener;

import javax.ejb.MessageDriven;
import javax.inject.Inject;
import javax.interceptor.Interceptors;

@Interceptors({CountInterceptor.class})
@MessageDriven(name = "Count")
public class InvocationCountCommand implements CrestListener {

    @Inject
    private CommandCounter counter;

    @Command
    public String invocations() {
        return Long.toString(counter.getCount());
    }

}
