package org.jboss.jdf.example.ticketmonster.command;

import org.tomitribe.crest.connector.api.TerminalSessionScoped;

import java.util.concurrent.atomic.AtomicLong;


@TerminalSessionScoped
public class CommandCounter {

    private AtomicLong invocationCount = new AtomicLong(0);

    public long getCount() {
        return invocationCount.get();
    }

    public void increment() {
        invocationCount.incrementAndGet();
    }
}
