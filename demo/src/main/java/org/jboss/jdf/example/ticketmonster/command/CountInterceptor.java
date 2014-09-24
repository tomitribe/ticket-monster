package org.jboss.jdf.example.ticketmonster.command;

import org.jboss.jdf.example.ticketmonster.cdi.scope.TerminalSessionScoped;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import java.util.logging.Logger;

@Count
@Interceptor
public class CountInterceptor {

    @Inject
    private CommandCounter counter;

    @PostConstruct
    public void construct(InvocationContext invocationContext) throws Exception {
        final Logger logger = Logger.getLogger(CountInterceptor.class.getName());
        logger.info("Counter: " + counter.getClass().getName());
        invocationContext.proceed();
    }

    @AroundInvoke
    public Object invoke(InvocationContext ic) throws Exception {
        counter.increment();
        return ic.proceed();
    }
}
