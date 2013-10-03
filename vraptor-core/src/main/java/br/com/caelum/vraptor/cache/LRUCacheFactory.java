package br.com.caelum.vraptor.cache;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;

@ApplicationScoped
public class LRUCacheFactory {
	
	@SuppressWarnings("rawtypes")	
	@Produces
	@br.com.caelum.vraptor.cache.LRU
	public LRUCacheStore getCache(InjectionPoint ip){
		int capacity = ip.getAnnotated().getAnnotation(br.com.caelum.vraptor.cache.LRU.class).capacity();
		return new LRUCacheStore<>(capacity);
	}
}
