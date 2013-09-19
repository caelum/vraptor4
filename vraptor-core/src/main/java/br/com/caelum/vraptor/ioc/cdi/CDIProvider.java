package br.com.caelum.vraptor.ioc.cdi;

import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.servlet.ServletContext;

import br.com.caelum.vraptor.core.RequestExecution;
import br.com.caelum.vraptor.core.RequestInfo;
import br.com.caelum.vraptor.ioc.Container;
import br.com.caelum.vraptor.ioc.ContainerProvider;

public class CDIProvider implements ContainerProvider {

	@Inject
	private CDIBasedContainer container;

	@Inject
	private StereotypesRegistry stereotypesRegistry;

	@Inject
	private RequestExecution requestExecution;

	@Inject
	private BeanManager beanManager;

	@Override
	public void provideForRequest(RequestInfo request) {
		beanManager.fireEvent(request);
		requestExecution.execute();
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