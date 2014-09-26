/* =====================================================================
 *
 * Copyright (c) 2011 David Blevins.  All rights reserved.
 *
 * =====================================================================
 */
package org.jboss.jdf.ticketmonster.test.rest;

import javax.ws.rs.core.MediaType;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(value = ElementType.METHOD)
@Repeatable(Assertions.class)
public @interface Assertion {
    String[] params() default {};

    String accept() default MediaType.APPLICATION_JSON;

    int code() default 200;

    String content() default "";
}
