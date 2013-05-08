package br.com.caelum.vraptor.ioc.cdi;

import javax.enterprise.inject.Alternative;
import javax.inject.Inject;
import javax.servlet.FilterChain;

import br.com.caelum.vraptor.ioc.ComponentFactory;
import br.com.caelum.vraptor.ioc.RequestScoped;

@RequestScoped
@Alternative
public class CDIFilterChainFactory implements ComponentFactory<FilterChain>{

	@Inject
	private CDIRequestInfoFactory cdiRequestInfoFactory;

	public FilterChain getInstance(){
		return cdiRequestInfoFactory.producesRequestInfo().getChain();
	}
}
