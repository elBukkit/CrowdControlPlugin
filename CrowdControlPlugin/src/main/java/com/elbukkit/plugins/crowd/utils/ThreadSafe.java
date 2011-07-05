package com.elbukkit.plugins.crowd.utils;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A simple annotation denoting if the method is thread safe.
 * 
 * @author Andrew Querol(winsock)
 * @version 1.0
 */
@Documented
@Target(value = ElementType.METHOD)
@Retention(value = RetentionPolicy.RUNTIME)
public @interface ThreadSafe {

    String author() default "Andrew Querol(winsock)";

    String shortDescription() default "Indicates that the method is thread safe";

    String version() default "1.0";

}
