package br.com.caelum.vraptor.ioc.spring.components;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;

import br.com.caelum.vraptor.ioc.Component;

@Component
public class DummyComponentFactory{

	@Produces
	@RequestScoped
	public Foo getInstance() {
		return null;
	}

}
