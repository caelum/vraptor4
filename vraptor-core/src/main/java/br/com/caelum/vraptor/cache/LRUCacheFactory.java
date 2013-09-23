package br.com.caelum.vraptor.cache;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;

import br.com.caelum.vraptor.util.LRUCache;

@ApplicationScoped
public class LRUCacheFactory {
	
	@SuppressWarnings("rawtypes")	
	@Produces
	@br.com.caelum.vraptor.cache.LRUCache
	public LRUCache getCache(InjectionPoint ip){
		int capacity = ip.getAnnotated().getAnnotation(br.com.caelum.vraptor.cache.LRUCache.class).capacity();
		return new LRUCache<>(capacity);
	}
}
