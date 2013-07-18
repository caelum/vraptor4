package br.com.caelum.vraptor4.ioc.cdi;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.BeforeBeanDiscovery;

import br.com.caelum.vraptor4.core.BaseComponents;
import br.com.caelum.vraptor4.core.StereotypeInfo;
import br.com.caelum.vraptor4.interceptor.PackagesAcceptor;
import br.com.caelum.vraptor4.interceptor.WithAnnotationAcceptor;
import br.com.caelum.vraptor4x.others.LoggerFactory;

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
		registerVraptorSpecifics();
		registerCustomAcceptors();
	}

	private void registerCustomAcceptors() {
		register(PackagesAcceptor.class);
		register(WithAnnotationAcceptor.class);
	}

	private void registerVraptorSpecifics() {
		register(StereotypesRegistry.class);
		register(LoggerFactory.class);
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
		registerComponents(getStereotypeHandlers());
	}

	private Set<Class<?>> getStereotypeHandlers() {			
		Set<Class<?>> handlers = new HashSet<Class<?>>();
		for (StereotypeInfo stereotypeInfo : BaseComponents.getStereotypesInfo()) {
			handlers.add(stereotypeInfo.getStereotypeClass());
		}
		return handlers;
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
