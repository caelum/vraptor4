package br.com.caelum.vraptor.ioc.cdi;

import javax.enterprise.inject.spi.BeanManager;
import javax.servlet.ServletContext;

import br.com.caelum.vraptor.core.Execution;
import br.com.caelum.vraptor.core.RequestInfo;
import br.com.caelum.vraptor.ioc.Container;
import br.com.caelum.vraptor.ioc.ContainerProvider;

public class CDIProvider implements ContainerProvider {

	public static final String BEAN_MANAGER_KEY = "javax.enterprise.inject.spi.BeanManager";
	private CDIBasedContainer container;
	private BeanManager beanManager;
	
	public <T> T provideForRequest(RequestInfo request, Execution<T> execution) {		
		beanManager.fireEvent(request);
		return execution.insideRequest(container);
	}

	public void stop() {
	}

	public void start(ServletContext context) {
		beanManager = (BeanManager) context.getAttribute(BEAN_MANAGER_KEY);
		if(beanManager==null){
			throw new IllegalStateException("ServletContext should have the "+BEAN_MANAGER_KEY+" key");
		}
		container = new CDIBasedContainer(beanManager);
		new StereotypesRegistry(beanManager).configure();
	}

	public Container getContainer() {
		return container;
	}

}
