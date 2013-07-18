package br.com.caelum.vraptor4.ioc.cdi;

import javax.annotation.Priority;
import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.servlet.http.HttpSession;

import br.com.caelum.vraptor4.ioc.RequestScoped;

@RequestScoped
@Alternative
@Priority(1000)
public class CDIHttpSessionFactory{
	
	@Inject
	private CDIRequestInfoFactory cdiRequestInfoFactory;	

	@Produces @javax.enterprise.context.RequestScoped
	public HttpSession getInstance(){
		return cdiRequestInfoFactory.producesRequestInfo().getRequest().getSession();
	}
}
