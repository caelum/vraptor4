package br.com.caelum.vraptor.ioc.cdi;

import javax.annotation.Priority;
import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.servlet.FilterChain;

import br.com.caelum.vraptor.ioc.RequestScoped;

@RequestScoped
@Alternative
@Priority(1000)
public class CDIFilterChainFactory{

	@Inject
	private CDIRequestInfoFactory cdiRequestInfoFactory;

	@Produces @javax.enterprise.context.RequestScoped
	public FilterChain getInstance(){
		return cdiRequestInfoFactory.producesRequestInfo().getChain();
	}
}
