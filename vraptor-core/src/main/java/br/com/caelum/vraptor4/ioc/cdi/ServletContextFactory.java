package br.com.caelum.vraptor4.ioc.cdi;

import javax.annotation.Priority;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Produces;
import javax.interceptor.Interceptor;
import javax.servlet.ServletContext;

import br.com.caelum.vraptor4.ioc.ApplicationScoped;

@ApplicationScoped
@Alternative
@Priority(Interceptor.Priority.LIBRARY_BEFORE)
public class ServletContextFactory{

	private ServletContext context;

	public void observesContext(@Observes ServletContext context){
		this.context = context;
	}

	@Produces
	@javax.enterprise.context.ApplicationScoped
	public ServletContext getInstance(){
		return this.context;
	}
}
