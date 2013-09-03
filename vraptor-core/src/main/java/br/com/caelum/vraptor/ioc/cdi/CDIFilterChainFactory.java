package br.com.caelum.vraptor.ioc.cdi;

import javax.annotation.Priority;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.interceptor.Interceptor;
import javax.servlet.FilterChain;

@RequestScoped
@Alternative
@Priority(Interceptor.Priority.LIBRARY_BEFORE)
public class CDIFilterChainFactory{

	@Inject
	private CDIRequestInfoFactory cdiRequestInfoFactory;

	@Produces @javax.enterprise.context.RequestScoped
	public FilterChain getInstance(){
		return cdiRequestInfoFactory.producesRequestInfo().getChain();
	}
}
