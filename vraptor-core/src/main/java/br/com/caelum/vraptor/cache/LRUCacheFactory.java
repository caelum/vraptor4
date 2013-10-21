package br.com.caelum.vraptor.cache;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;

@ApplicationScoped
public class LRUCacheFactory {
	
	@SuppressWarnings("rawtypes")	
	@Produces
	@LRU
	public LRUCacheStore getCache(InjectionPoint ip){
		int capacity = ip.getAnnotated().getAnnotation(LRU.class).capacity();
		return new LRUCacheStore<>(capacity);
	}
}
