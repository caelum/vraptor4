package br.com.caelum.vraptor.cache;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.inject.Qualifier;

/**
 * Used to mark an cache implementation as LRUCache.
 * @author Alberto Souza
 *
 */
@Qualifier
@Target(value={ElementType.TYPE,ElementType.FIELD,ElementType.PARAMETER})
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface LRUCache {

}
