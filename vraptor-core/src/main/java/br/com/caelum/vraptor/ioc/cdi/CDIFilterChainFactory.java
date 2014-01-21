package br.com.caelum.vraptor.ioc.cdi;

import javax.annotation.Priority;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.interceptor.Interceptor;
import javax.servlet.FilterChain;

import br.com.caelum.vraptor.core.RequestInfo;

@RequestScoped
@Alternative
@Priority(Interceptor.Priority.LIBRARY_BEFORE)
public class CDIFilterChainFactory{

	@Inject
	private RequestInfo requestInfo;

	@Produces
	@RequestScoped
	public FilterChain getInstance(){
		return requestInfo.getChain();
	}
}
