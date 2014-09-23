package org.jboss.jdf.example.ticketmonster.command;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Singleton;
import javax.inject.Inject;

import org.jboss.jdf.example.ticketmonster.cdi.scope.TerminalSessionScopeContext;

@Singleton
public class ScopeStarter {

    @Inject
    private TerminalSessionScopeContext context;
    
    @PostConstruct
    public void postConstruct() {
        context.create();
        context.begin();
    }
    
    @PreDestroy
    public void preDestroy() {
        context.end();
        context.destroy();
    }
    
}
