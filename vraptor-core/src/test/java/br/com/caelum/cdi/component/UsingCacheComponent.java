package br.com.caelum.cdi.component;

import javax.inject.Inject;

import br.com.caelum.vraptor.cache.LRU;
import br.com.caelum.vraptor.cache.Cache;

public class UsingCacheComponent {

	@Inject
	@LRU(capacity=200)
	private Cache<String,String> cacheLRU;
	
	@Inject
	private Cache<String,String> cache;
	
	public String putWithLRU(String key,String value) {
		return cacheLRU.put(key, value);
	}
	
	public String putWithDefault(String key,String value) {
		return cache.put(key, value);
	}	
}
