package org.jboss.jdf.example.ticketmonster.cdi.scope;

import javax.enterprise.context.spi.Context;
import javax.enterprise.context.spi.Contextual;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class TerminalSessionCDIContextImpl implements Context {

    public static final ThreadLocalState state = new ThreadLocalState();

    public Class<? extends Annotation> getScope() {
        return TerminalSessionScoped.class;
    }

    public <T> T get(Contextual<T> contextual, CreationalContext<T> creationalContext) {
        Bean<T> bean = (Bean<T>) contextual;

        Iterator<ScopedInstance<?>> iterator = state.allInstances.iterator();
        while (iterator.hasNext()) {
            ScopedInstance<?> scopedInstance = (ScopedInstance<?>) iterator.next();
            if (bean.getBeanClass().equals(scopedInstance.bean.getBeanClass())) {
                return (T) scopedInstance.instance;
            }
        }

        ScopedInstance<T> si = new ScopedInstance<T>();
        si.bean = bean;
        si.ctx = creationalContext;
        si.instance = bean.create(creationalContext);
        state.allInstances.add(si);
        return (T) si.instance;
    }

    public <T> T get(Contextual<T> contextual) {
        throw new IllegalArgumentException();
    }

    public boolean isActive() {
        return state != null ? true : false;
    }

    public static class ThreadLocalState {
        //These should be converted to thread safe collections
        Set<ScopedInstance<?>> allInstances = new HashSet<ScopedInstance<?>>();

        public void destroy() {
            //Since this is not a CDI NormalScope we are responsible for managing the entire lifecycle, including
            //destroying the beans
            for (ScopedInstance entry2 : state.allInstances) {
                entry2.bean.destroy(entry2.instance, entry2.ctx);
            }
        }

    }

    public static class ScopedInstance<T> {
        Bean<T> bean;
        CreationalContext<T> ctx;
        T instance;
    }
}