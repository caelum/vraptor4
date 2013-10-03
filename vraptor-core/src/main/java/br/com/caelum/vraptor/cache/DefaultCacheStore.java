package br.com.caelum.vraptor.cache;

import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;

import javax.enterprise.inject.Default;

import com.google.common.base.Throwables;

@Default
public class DefaultCacheStore<K,V> implements CacheStore<K,V> {

	private final ConcurrentHashMap<K,V> cache = new ConcurrentHashMap<>();

	@Override
	public V fetch(K key, Callable<V> valueProvider) {
		if (!cache.containsKey(key)){
			try {
				cache.put(key, valueProvider.call());
			} catch (Exception e) {
				Throwables.propagateIfPossible(e);
				throw new CacheException("Error computing the value", e);
			}
		}
		return cache.get(key);
	}
}
