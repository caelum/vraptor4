package br.com.caelum.vraptor.interceptor;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that this interceptor has a static accepts. If it returns false once, it will
 * always returns false.
 *
 * @author Alberto Souza
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface StaticAccepts {

}
