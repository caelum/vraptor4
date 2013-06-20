package br.com.caelum.vraptor.ioc.cdi;

import javax.annotation.Priority;
import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import br.com.caelum.vraptor.http.MutableRequest;
import br.com.caelum.vraptor.ioc.RequestScoped;

/**
 * It is a isolated factory because some containers could provide 
 * @author Alberto Souza
 *
 */
@RequestScoped
@Alternative
@Priority(1000)
public class CDIHttpServletRequestFactory{

	@Inject
	private CDIRequestInfoFactory cdiRequestInfoFactory;
	
	@Produces @javax.enterprise.context.RequestScoped
	public MutableRequest getInstance(){
		return cdiRequestInfoFactory.producesRequestInfo().getRequest();
	}

}
