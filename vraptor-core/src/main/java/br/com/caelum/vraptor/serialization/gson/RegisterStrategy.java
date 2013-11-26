package br.com.caelum.vraptor.serialization.gson;

import static br.com.caelum.vraptor.serialization.gson.RegisterType.INHERITANCE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Defines strategies for registering custom serialization or deserialization in
 * Gson.
 */
@Target({ TYPE })
@Retention(RUNTIME)
public @interface RegisterStrategy {

	/** The strategy to be used for the entity inheritance hierarchy. */
	RegisterType value() default INHERITANCE;
}