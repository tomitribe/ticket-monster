package org.jboss.jdf.example.ticketmonster.command;

import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

@Count
@Interceptor
public class CountInterceptor {

    @Inject
    private CommandCounter counter;

    @AroundInvoke
    public Object invoke(InvocationContext ic) throws Exception {
        counter.increment();
        return ic.proceed();
    }
}
