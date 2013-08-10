package br.com.caelum.vraptor4.ioc.cdi;

import javax.annotation.Priority;
import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.interceptor.Interceptor;
import javax.servlet.FilterChain;

import br.com.caelum.vraptor4.ioc.RequestScoped;

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
