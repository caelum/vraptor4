package br.com.caelum.vraptor.ioc.cdi;

import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.servlet.ServletContext;

import br.com.caelum.vraptor.core.Execution;
import br.com.caelum.vraptor.core.RequestInfo;
import br.com.caelum.vraptor.ioc.Container;
import br.com.caelum.vraptor.ioc.ContainerProvider;

public class CDIProvider implements ContainerProvider {

	@Inject
	private CDIBasedContainer container;
	@Inject
	private BeanManager beanManager;
	
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
		new StereotypesRegistry(beanManager).configure();
	}

	@Override
	public Container getContainer() {
		return container;
	}

}
