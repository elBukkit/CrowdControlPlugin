package com.elBukkit.bukkit.plugins.crowd;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/*
 * A simple annotation denoting if the method is thread safe.
 * 
 * @author Andrew Querol(winsock)
 */

@Documented
@Target(value = ElementType.METHOD)
@Retention(value = RetentionPolicy.RUNTIME)
public @interface ThreadSafe {

	public String author() default "Andrew Querol(winsock)";

	public String shortDescription() default "Indicates that the method is thread safe";

	public String version() default "1.0";

}
