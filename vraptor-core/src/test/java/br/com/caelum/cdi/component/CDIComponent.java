package br.com.caelum.cdi.component;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;

import br.com.caelum.vraptor.ioc.Component;
import br.com.caelum.vraptor.ioc.GenericContainerTest.MyRequestComponent;
import br.com.caelum.vraptor.ioc.cdi.ComponentToBeProduced;

@Component
public class CDIComponent{
	
	private MyRequestComponent component;

	//CDI eyes only
	@Deprecated
	public CDIComponent() {
	}
	
	public CDIComponent(MyRequestComponent component) {
		this.component = component;
	}

	@Produces
	@RequestScoped
	public ComponentToBeProduced getInstance() {		
		return new ComponentToBeProduced();
	}
}
