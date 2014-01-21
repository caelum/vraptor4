package br.com.caelum.vraptor.ioc.cdi;

import javax.annotation.Priority;
import javax.enterprise.context.Dependent;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.interceptor.Interceptor;

import br.com.caelum.vraptor.core.RequestInfo;
import br.com.caelum.vraptor.http.MutableResponse;

@RequestScoped
@Alternative
@Priority(Interceptor.Priority.LIBRARY_BEFORE)
public class CDIHttpServletResponseFactory{

	@Inject
	private RequestInfo requestInfo;

	@Produces
	@Dependent
	public MutableResponse getInstance(){
		return requestInfo.getResponse();
	}
}
