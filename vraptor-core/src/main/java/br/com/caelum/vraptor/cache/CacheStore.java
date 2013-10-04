package br.com.caelum.vraptor.cache;

import java.util.concurrent.Callable;

/**
 *
 * @author Lucas Cavalcanti
 * @author Alberto Souza
 * @since 4.0.0
 * @param <K> type of the cache key
 * @param <V> type of the cache value
 */
public interface CacheStore<K,V> {

	/**
	 * Writes a value on the cache
	 *
	 * @param key
	 * @param value
	 * @return the previous stored value, null if none.
	 */
	public V write(K key, V value);

	/**
	 * Fetches the value under the key
	 * @param key
	 * @return the stored value for the key, or null.
	 */
	public V fetch(K key);

	/**
	 * Fetches the value under the key. If no value is set, valueProvider is used to
	 * compute a value for the key. Once the value is computed, it is stored on the cache.
	 *
	 * @param key
	 * @param valueProvider
	 * @return the stored or the new value for the provided key.
	 */
	public V fetch(K key, Callable<V> valueProvider);

}
