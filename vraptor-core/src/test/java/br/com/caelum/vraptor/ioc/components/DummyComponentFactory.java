package br.com.caelum.vraptor.ioc.components;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;

public class DummyComponentFactory{

	@Produces
	@RequestScoped
	public Foo getInstance() {
		return null;
	}

}
