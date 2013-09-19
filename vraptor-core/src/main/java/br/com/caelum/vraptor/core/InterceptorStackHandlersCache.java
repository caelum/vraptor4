package br.com.caelum.vraptor.core;

import java.util.LinkedList;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import br.com.caelum.vraptor.interceptor.InterceptorRegistry;

@ApplicationScoped
public class InterceptorStackHandlersCache {
	
	private InterceptorRegistry registry;
	private InterceptorHandlerFactory handlerFactory;
	
	private final LinkedList<InterceptorHandler> interceptorHandlers = new LinkedList<>();
	
	@Deprecated //CDI eyes only
	public InterceptorStackHandlersCache() {}

	@Inject
	public InterceptorStackHandlersCache(InterceptorRegistry registry,InterceptorHandlerFactory handlerFactory){
		this.registry = registry;
		this.handlerFactory = handlerFactory;

		for (Class<?> interceptor : registry.all()) {
			this.interceptorHandlers.addLast(handlerFactory.handlerFor(interceptor));
		}
	}
	
	public LinkedList<InterceptorHandler> getInterceptorHandlers() {
		return new LinkedList<>(interceptorHandlers);
	}

}
