package org.jboss.jdf.example.ticketmonster.cdi.scope;

import javax.enterprise.context.Dependent;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Inject;
import java.util.HashSet;
import java.util.Set;

@Dependent
public class TerminalSessionScopeContext {

    public static ThreadLocalState state = new ThreadLocalState();

    static class ThreadLocalState {
        //These should be converted to thread safe collections
        Set<ScopedInstance<?>> allInstances = new HashSet<ScopedInstance<?>>();
    }

    @Inject
    BeanManager bm;

    public void create() {
    }

    public void begin() {
        state = new ThreadLocalState();
    }

    public void end() {
    }

    public <T> T newInstance(Class<T> clazz) {
        Set<Bean<?>> beans = bm.getBeans(clazz, new AnnotationLiteral<Any>() {
        });
        if (beans.size() > 0) {
            Bean<?> bean = beans.iterator().next();
            CreationalContext<?> cc = bm.createCreationalContext(bean);
            return (T) bm.getReference(bean, clazz, cc);
        }
        return null;
    }

    public void destroy() {
        //Since this is not a CDI NormalScope we are responsible for managing the entire lifecycle, including
        //destroying the beans
        for (ScopedInstance entry2 : state.allInstances) {
            entry2.bean.destroy(entry2.instance, entry2.ctx);
        }
    }

    public static class ScopedInstance<T> {
        Bean<T> bean;
        CreationalContext<T> ctx;
        T instance;
    }

}
