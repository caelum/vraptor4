package br.com.caelum.cdi.component;

import javax.inject.Inject;

import br.com.caelum.vraptor.cache.LRU;
import br.com.caelum.vraptor.cache.VRaptorCache;

public class UsingCacheComponent {

	@Inject
	@LRU(capacity=200)
	private VRaptorCache<String,String> cacheLRU;
	
	@Inject
	private VRaptorCache<String,String> cache;
	
	public String putWithLRU(String key,String value) {
		return cacheLRU.put(key, value);
	}
	
	public String putWithDefault(String key,String value) {
		return cache.put(key, value);
	}	
}
