package br.com.caelum.vraptor.ioc.cdi;

import java.util.Arrays;
import java.util.Collection;

import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.BeforeBeanDiscovery;

import br.com.caelum.vraptor.core.BaseComponents;

public class CDIRegistry {

	private BeforeBeanDiscovery discovery;
	private BeanManager bm;

	public CDIRegistry(BeforeBeanDiscovery discovery, BeanManager bm) {
		this.discovery = discovery;
		this.bm = bm;	
	}
	
	public void configure(){
		registerApplicationComponents();
		registerRequestComponents();
		registerPrototypeComponents();	
		registerConverters();
		registerCDISpecifics();
		registerProvider();
	}

	private void registerProvider() {
		register(CDIProvider.class);
	}

	private void registerConverters() {
		registerComponents(BaseComponents.getBundledConverters());
	}

	private void registerCDISpecifics() {
		register(CDIBasedContainer.class);
		register(CDIRequestInfoFactory.class);
		register(ServletContextFactory.class);
		register(CDIHttpServletRequestFactory.class);
		register(CDIHttpServletResponseFactory.class);
		register(CDIFilterChainFactory.class);
		register(CDIHttpSessionFactory.class);
		register(ListProducer.class);
	}

	private void registerPrototypeComponents() {
		registerComponents(BaseComponents.getPrototypeScoped().values());
	}

	private void registerRequestComponents() {
		registerComponents(BaseComponents.getRequestScoped().values());
	}

	private void registerApplicationComponents() {
		registerComponents(BaseComponents.getApplicationScoped().values());
		registerComponents(Arrays.asList(BaseComponents.getStereotypeHandlers()));
	}
	
	private <T> void registerComponents(Collection<Class<? extends T>> toRegister) {
		for (Class<?> component : toRegister){
			register(component);
		}
	}
	
	private void register(Class<?> component) {			
		discovery.addAnnotatedType(bm.createAnnotatedType(component));
	}
	
}
