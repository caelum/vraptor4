package br.com.caelum.vraptor.cache;

import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.enterprise.inject.Default;

import com.google.common.base.Throwables;

@Default
public class DefaultCacheStore<K,V> implements CacheStore<K,V> {

	private final ConcurrentMap<K,V> cache = new ConcurrentHashMap<>();

	@Override
	public V fetch(K key, Callable<V> valueProvider) {
		if (!cache.containsKey(key)){
			try {
				V value = valueProvider.call();
				cache.put(key, value);
				return value;
			} catch (Exception e) {
				Throwables.propagateIfPossible(e);
				throw new CacheException("Error computing the value", e);
			}
		}
		return cache.get(key);
	}

	@Override
	public V write(K key, V value) {
		return cache.put(key, value);
	}

	@Override
	public V fetch(K key) {
		return cache.get(key);
	}
}
