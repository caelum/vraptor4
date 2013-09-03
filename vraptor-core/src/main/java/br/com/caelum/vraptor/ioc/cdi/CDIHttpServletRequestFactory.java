package br.com.caelum.vraptor.ioc.cdi;

import javax.annotation.Priority;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.interceptor.Interceptor;

import br.com.caelum.vraptor.http.MutableRequest;

/**
 * It is a isolated factory because some containers could provide
 * @author Alberto Souza
 *
 */
@RequestScoped
@Alternative
@Priority(Interceptor.Priority.LIBRARY_BEFORE)
public class CDIHttpServletRequestFactory{

	@Inject
	private CDIRequestInfoFactory cdiRequestInfoFactory;

	@Produces @javax.enterprise.context.RequestScoped
	public MutableRequest getInstance(){
		return cdiRequestInfoFactory.producesRequestInfo().getRequest();
	}

}
