package br.com.caelum.vraptor.cache;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.enterprise.util.Nonbinding;
import javax.inject.Qualifier;

/**
 * Used to mark an cache implementation as LRUCache.
 * @author Alberto Souza
 *
 */
@Qualifier
@Target(value={ElementType.TYPE,ElementType.FIELD,ElementType.PARAMETER,ElementType.METHOD})
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface LRU {
	
	@Nonbinding
	int capacity() default 100;

}
