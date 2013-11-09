package br.com.caelum.vraptor.interceptor;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Notifies VRaptor to include all parameters of a method
 * annotated with this {@link IncludeParameters} annotation
 *
 * @author Rodrigo Turini
 * @since 4.0
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface IncludeParameters {
}