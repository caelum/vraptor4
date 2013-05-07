package br.com.caelum.vraptor.ioc.cdi;

import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.servlet.http.HttpSession;

import br.com.caelum.vraptor.ioc.ComponentFactory;
import br.com.caelum.vraptor.ioc.RequestScoped;

@RequestScoped
@Alternative
public class CDIHttpSessionFactory implements ComponentFactory<HttpSession>{
	
	@Inject
	private CDIRequestInfoFactory cdiRequestInfoFactory;	

	public HttpSession getInstance(){
		return cdiRequestInfoFactory.producesRequestInfo().getRequest().getSession();
	}
}
