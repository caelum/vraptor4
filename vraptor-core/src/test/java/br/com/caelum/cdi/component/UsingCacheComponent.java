package br.com.caelum.cdi.component;

import javax.inject.Inject;

import br.com.caelum.vraptor.cache.CacheStore;
import br.com.caelum.vraptor.cache.LRU;

public class UsingCacheComponent {

	@Inject
	@LRU(capacity=200)
	private CacheStore<String,String> cacheLRU;

	@Inject
	private CacheStore<String,String> cache;

	public String putWithLRU(String key,String value) {
		return cacheLRU.write(key, value);
	}

	public String putWithDefault(String key,String value) {
		return cache.write(key, value);
	}
}
