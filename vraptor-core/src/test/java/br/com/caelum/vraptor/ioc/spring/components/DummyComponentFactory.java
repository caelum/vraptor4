package br.com.caelum.vraptor.ioc.spring.components;

import javax.enterprise.inject.Produces;

import br.com.caelum.vraptor.ioc.Component;
import br.com.caelum.vraptor.ioc.ComponentFactory;

@Component
public class DummyComponentFactory implements ComponentFactory<Foo>{

	@Produces
	public Foo getInstance() {
		return null;
	}

}
