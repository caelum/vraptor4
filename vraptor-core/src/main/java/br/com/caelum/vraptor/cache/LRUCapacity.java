package br.com.caelum.vraptor.cache;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.inject.Qualifier;

/**
 * Used to create producers in order to be used to create
 * LRUCache's
 * @author Alberto Souza
 *
 */
@Qualifier
@Target(value={ElementType.FIELD,ElementType.PARAMETER,ElementType.METHOD})
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface LRUCapacity {

}
