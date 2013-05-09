package br.com.caelum.vraptor.ioc.cdi;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Produces;
import javax.servlet.ServletContext;

import br.com.caelum.vraptor.ioc.ApplicationScoped;

@ApplicationScoped
@Alternative
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
