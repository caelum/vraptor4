package br.com.caelum.vraptor4.ioc.cdi;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Produces;

import br.com.caelum.vraptor4.core.RequestInfo;

@RequestScoped
public class CDIRequestInfoFactory {
	
	private RequestInfo requestInfo;

	public void observesRequest(@Observes RequestInfo requestInfo){
		this.requestInfo = requestInfo;
	}
	
	@Produces
	public RequestInfo producesRequestInfo(){
		return requestInfo;
	}		
	
}
