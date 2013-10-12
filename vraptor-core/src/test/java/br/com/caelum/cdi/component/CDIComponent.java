package br.com.caelum.cdi.component;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;

import br.com.caelum.vraptor.ioc.cdi.ComponentToBeProduced;

public class CDIComponent{
	
	@Produces
	@RequestScoped
	public ComponentToBeProduced getInstance() {		
		return new ComponentToBeProduced();
	}
}
