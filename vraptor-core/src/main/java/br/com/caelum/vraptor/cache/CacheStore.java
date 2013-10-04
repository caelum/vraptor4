package br.com.caelum.vraptor.cache;

import java.util.concurrent.Callable;

public interface CacheStore<K,V> {

	public void write(K key, V value);

	public V fetch(K key);

	public V fetch(K key, Callable<V> valueProvider);

}
