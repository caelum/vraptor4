package br.com.caelum.vraptor.ioc.cdi;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

import br.com.caelum.vraptor.core.RequestInfo;
import br.com.caelum.vraptor.ioc.ContainerProvider;

@Dependent
public class CDIProvider implements ContainerProvider {

	@Inject
	private StereotypesRegistry stereotypesRegistry;

	@Inject
	private BeanManager beanManager;

	@Override
	public void provideForRequest(RequestInfo request) {
		beanManager.fireEvent(request);
	}

	@Override
	public void stop() {
	}

	@Override
	public void start() {
		stereotypesRegistry.configure();
	}

}