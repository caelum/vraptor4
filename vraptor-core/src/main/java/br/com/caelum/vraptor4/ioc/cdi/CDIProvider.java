package br.com.caelum.vraptor4.ioc.cdi;

import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.servlet.ServletContext;

import br.com.caelum.vraptor4.core.Execution;
import br.com.caelum.vraptor4.core.RequestInfo;
import br.com.caelum.vraptor4.ioc.Container;
import br.com.caelum.vraptor4.ioc.ContainerProvider;

public class CDIProvider implements ContainerProvider {

	@Inject
	private CDIBasedContainer container;
	@Inject
	private BeanManager beanManager;
	@Inject
	private StereotypesRegistry stereotypesRegistry;
	
	@Override
	public <T> T provideForRequest(RequestInfo request, Execution<T> execution) {		
		beanManager.fireEvent(request);
		return execution.insideRequest(container);
	}

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
