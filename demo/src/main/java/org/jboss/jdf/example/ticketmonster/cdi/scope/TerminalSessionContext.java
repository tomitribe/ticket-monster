package org.jboss.jdf.example.ticketmonster.cdi.scope;

import javax.enterprise.context.spi.Context;
import javax.enterprise.context.spi.Contextual;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.logging.Logger;

public class TerminalSessionContext implements Context {

    private static final Logger logger = Logger.getLogger(TerminalSessionContext.class.getName());

    public static final ThreadLocal<TerminalState> state = new ThreadLocal<TerminalState>() {
        @Override
        protected TerminalState initialValue() {
            return new TerminalState();
        }
    };

    public Class<? extends Annotation> getScope() {
        return TerminalSessionScoped.class;
    }

    public <T> T get(Contextual<T> contextual, CreationalContext<T> creationalContext) {
        final Bean<T> bean = (Bean<T>) contextual;

        final TerminalState terminalState = state.get();

        if (terminalState == null) return null;

        return terminalState.getInstance(creationalContext, bean);
    }

    public <T> T get(Contextual<T> contextual) {
        throw new IllegalArgumentException();
    }

    public boolean isActive() {
        // This will never be null due to ThreadLocal.initialvalue() overriding
        return state.get() != null;
    }

    public static class TerminalState {

        private static int ids = 1000;
        private final int id = ids++;

        //These should be converted to thread safe collections
        private final Map<Class<?>, ScopedInstance<?>> map = new ConcurrentHashMap<Class<?>, ScopedInstance<?>>();

        public TerminalState() {
            logger.info(m("Constructed"));
        }

        private <T> T getInstance(final CreationalContext<T> creationalContext, final Bean<T> bean) {
            final Class<?> beanClass = bean.getBeanClass();

            logger.info(m("getInstance(%s)", beanClass.getName()));

            final ScopedInstance<?> scopedInstance = map.computeIfAbsent(beanClass, new Function<Class<?>, ScopedInstance<?>>() {
                @Override
                public ScopedInstance<?> apply(final Class<?> ignored) {
                    logger.info(m("create(%s)", beanClass.getName()));
                    return new ScopedInstance<T>(bean, creationalContext, bean.create(creationalContext));
                }
            });

            return (T) scopedInstance.getInstance();
        }

        public void destroy() {
            logger.info(m("destroying context"));
            //Since this is not a CDI NormalScope we are responsible for managing the entire lifecycle, including
            //destroying the beans
            for (ScopedInstance scopedInstance : map.values()) {
                logger.info(m("destroy(%s)", scopedInstance.getBean().getBeanClass().getName()));
                scopedInstance.getBean().destroy(scopedInstance.getInstance(), scopedInstance.getCreationalContext());
            }

            map.clear();
        }

        private String m(String format, Object... data) {
            final String message = String.format(format, data);
            return String.format("[%s] session(%s:%s) - %s", Thread.currentThread().getName(), id, ids, message);
        }
    }

    public static class ScopedInstance<T> {
        private final Bean<T> bean;
        private final CreationalContext<T> creationalContext;
        private final T instance;

        public ScopedInstance(final Bean<T> bean, final CreationalContext<T> creationalContext, final T instance) {
            this.bean = bean;
            this.creationalContext = creationalContext;
            this.instance = instance;
        }

        public Bean<T> getBean() {
            return bean;
        }

        public CreationalContext<T> getCreationalContext() {
            return creationalContext;
        }

        public T getInstance() {
            return instance;
        }
    }
}