package org.jboss.jdf.example.ticketmonster.command;

import java.util.concurrent.atomic.AtomicLong;

import org.jboss.jdf.example.ticketmonster.cdi.scope.TerminalSessionScoped;

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
