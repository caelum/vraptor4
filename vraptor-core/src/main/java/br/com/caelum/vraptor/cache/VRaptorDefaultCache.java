package br.com.caelum.vraptor.cache;

import java.util.concurrent.ConcurrentHashMap;

import javax.enterprise.inject.Default;

@Default
public class VRaptorDefaultCache<K,V> implements VRaptorCache<K,V>{
	
	private final ConcurrentHashMap<K,V> cache = new ConcurrentHashMap<>();			
		
	@Override
	public V get(K key) {
		return cache.get(key);
	}

	@Override
	public V put(K key, V value) {
		return cache.put(key, value);
	}

	@Override
	public V putIfAbsent(K key, V value) {
		return cache.putIfAbsent(key, value);
	}

}
