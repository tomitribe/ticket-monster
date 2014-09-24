package org.jboss.jdf.example.ticketmonster.cdi.scope;

import javax.enterprise.context.NormalScope;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@NormalScope
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD})
public @interface TerminalSessionScoped {
}