package br.com.caelum.vraptor.ioc.cdi;

import javax.inject.Inject;
import javax.servlet.ServletContext;

import br.com.caelum.vraptor.ioc.Container;
import br.com.caelum.vraptor.ioc.ContainerProvider;

public class CDIProvider implements ContainerProvider {

	@Inject
	private CDIBasedContainer container;

	@Inject
	private StereotypesRegistry stereotypesRegistry;

	@Override
	public void stop() {
	}

	@Override
	public void start(ServletContext context) {
		stereotypesRegistry.configure();
	}

	@Override
	public Container getContainer() {
		return container;
	}

}
