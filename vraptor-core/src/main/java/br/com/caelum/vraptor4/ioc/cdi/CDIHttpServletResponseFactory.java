package br.com.caelum.vraptor4.ioc.cdi;

import javax.annotation.Priority;
import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.interceptor.Interceptor;

import br.com.caelum.vraptor4.http.MutableResponse;
import br.com.caelum.vraptor4.ioc.RequestScoped;

@RequestScoped
@Alternative
@Priority(Interceptor.Priority.LIBRARY_BEFORE)
public class CDIHttpServletResponseFactory{

	@Inject
	private CDIRequestInfoFactory cdiRequestInfoFactory;

	@Produces @javax.enterprise.context.RequestScoped
	public MutableResponse getInstance(){
		return cdiRequestInfoFactory.producesRequestInfo().getResponse();
	}
}
