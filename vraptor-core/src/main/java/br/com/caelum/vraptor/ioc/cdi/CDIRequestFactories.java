package br.com.caelum.vraptor.ioc.cdi;

import javax.annotation.Priority;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Produces;
import javax.interceptor.Interceptor;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpSession;

import br.com.caelum.vraptor.events.NewRequest;
import br.com.caelum.vraptor.http.MutableRequest;
import br.com.caelum.vraptor.http.MutableResponse;

@RequestScoped
@Alternative
@Priority(Interceptor.Priority.LIBRARY_BEFORE)
public class CDIRequestFactories {

	private NewRequest newRequest;

	public void setRequest(NewRequest info) {
		this.newRequest = info;
	}
	
	@Produces
	@RequestScoped
	public HttpSession getSession(){
		return newRequest.getRequest().getSession();
	}
	
	@Produces
	@RequestScoped
	public MutableResponse getResponse(){
		return newRequest.getResponse();
	}
	
	@Produces
	@RequestScoped
	public MutableRequest getRequest(){
		return newRequest.getRequest();
	}
	
	@Produces
	@RequestScoped
	public FilterChain getChain(){
		return newRequest.getChain();
	}
}
