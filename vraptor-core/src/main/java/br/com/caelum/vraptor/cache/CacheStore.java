package br.com.caelum.vraptor.cache;

import java.util.concurrent.Callable;

public interface CacheStore<K,V> {

	public V fetch(K key, Callable<V> valueProvider);

}
