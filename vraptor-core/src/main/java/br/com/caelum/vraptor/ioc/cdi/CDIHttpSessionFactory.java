package br.com.caelum.vraptor.ioc.cdi;

import javax.annotation.Priority;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.interceptor.Interceptor;
import javax.servlet.http.HttpSession;

import br.com.caelum.vraptor.core.RequestInfo;

@RequestScoped
@Alternative
@Priority(Interceptor.Priority.LIBRARY_BEFORE)
public class CDIHttpSessionFactory{

	@Inject
	private RequestInfo requestInfo;

	@Produces
	@RequestScoped
	public HttpSession getInstance(){
		return requestInfo.getRequest().getSession();
	}
}
