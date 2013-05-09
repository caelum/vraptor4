package br.com.caelum.vraptor.ioc.cdi;

import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.servlet.http.HttpSession;

import br.com.caelum.vraptor.ioc.RequestScoped;

@RequestScoped
@Alternative
public class CDIHttpSessionFactory{
	
	@Inject
	private CDIRequestInfoFactory cdiRequestInfoFactory;	

	@Produces @javax.enterprise.context.RequestScoped
	public HttpSession getInstance(){
		return cdiRequestInfoFactory.producesRequestInfo().getRequest().getSession();
	}
}
