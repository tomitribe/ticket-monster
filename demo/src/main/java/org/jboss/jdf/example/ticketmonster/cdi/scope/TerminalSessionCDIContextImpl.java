package org.jboss.jdf.example.ticketmonster.cdi.scope;

import org.jboss.jdf.example.ticketmonster.cdi.scope.TerminalSessionScopeContext.ScopedInstance;
import org.jboss.jdf.example.ticketmonster.cdi.scope.TerminalSessionScopeContext.ThreadLocalState;

import javax.enterprise.context.spi.Context;
import javax.enterprise.context.spi.Contextual;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import java.lang.annotation.Annotation;
import java.util.Iterator;

public class TerminalSessionCDIContextImpl implements Context {

    public Class<? extends Annotation> getScope() {
        return TerminalSessionScoped.class;
    }

    public <T> T get(Contextual<T> contextual, CreationalContext<T> creationalContext) {
        Bean<T> bean = (Bean<T>) contextual;

        ThreadLocalState tscope = TerminalSessionScopeContext.state;

        Iterator<ScopedInstance<?>> iterator = tscope.allInstances.iterator();
        while (iterator.hasNext()) {
            TerminalSessionScopeContext.ScopedInstance<?> scopedInstance = (TerminalSessionScopeContext.ScopedInstance<?>) iterator.next();
            if (bean.getBeanClass().equals(scopedInstance.bean.getBeanClass())) {
                return (T) scopedInstance.instance;
            }
        }

        ScopedInstance<T> si = new ScopedInstance<T>();
        si.bean = bean;
        si.ctx = creationalContext;
        si.instance = bean.create(creationalContext);
        tscope.allInstances.add(si);
        return (T) si.instance;
    }

    public <T> T get(Contextual<T> contextual) {
        throw new IllegalArgumentException();
    }

    public boolean isActive() {
        return TerminalSessionScopeContext.state != null ? true : false;
    }

}