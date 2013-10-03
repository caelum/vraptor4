package br.com.caelum.vraptor.cache;

public interface Cache<K,V> {

	public V get(K key);
	
	public V put(K key,V value);
	
	public V putIfAbsent(K key,V value);
}
