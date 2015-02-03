package br.com.caelum.vraptor.ioc.cdi;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.inject.Qualifier;

/**
 * Means that the application scoped bean should be initialized early. 
 * @see br.com.caelum.vraptor.ioc.cdi.EagerApplicationScopedExtension
 * 
 * @author Rodrigo Turini
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Qualifier
public @interface EagerInitialized {
}
