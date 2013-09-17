package br.com.caelum.vraptor.core;

import java.util.LinkedList;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import br.com.caelum.vraptor.interceptor.InterceptorRegistry;

@ApplicationScoped
public class InterceptorStackHandlersCache {
	
	private InterceptorRegistry registry;
	private InterceptorHandlerFactory handlerFactory;
	
	private final LinkedList<InterceptorHandler> interceptors = new LinkedList<>();
	
	@Deprecated //CDI eyes only
	public InterceptorStackHandlersCache() {}

	@Inject
	public InterceptorStackHandlersCache(InterceptorRegistry registry,InterceptorHandlerFactory handlerFactory){
		this.registry = registry;
		this.handlerFactory = handlerFactory;
	}
	
	@PostConstruct
	public void cache(){
		for (Class<?> interceptor : registry.all()) {
			this.interceptors.addLast(handlerFactory.handlerFor(interceptor));
		}
	}
	
	public LinkedList<InterceptorHandler> getInterceptors() {
		return new LinkedList<>(interceptors);
	}

}
