package br.com.caelum.vraptor.ioc.cdi;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.inject.Qualifier;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Even with the Default CDI annotation, this one
 * is helpful when we need to prefer vraptor component instead of Container specific component.
 * Sometimes the container already provides producers for HttpServletRequest, HttpServletResponse and
 * so on. 
 * @author Alberto Souza
 *
 */
@Target( { TYPE, METHOD, PARAMETER, FIELD })
@Retention(RUNTIME)
@Documented
@Qualifier
public @interface VraptorPreference {

}
