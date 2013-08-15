package br.com.caelum.vraptor4.core;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.annotation.Priority;
import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Stereotype;
import javax.interceptor.Interceptor;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;


/**
 * Components annotated as PluginComponent will override default behaviour of VRaptor 
 * internal classes. This annotation should be used by plugin developers.
 * @author Mario Amaral
 * @since 4.0
 */

@Target(TYPE)
@Retention(RUNTIME)
@Documented
@Stereotype
@Alternative
@Priority(Interceptor.Priority.LIBRARY_BEFORE + 500)
public @interface PluginComponent {

}
