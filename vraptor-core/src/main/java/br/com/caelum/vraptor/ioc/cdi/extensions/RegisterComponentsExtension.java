package br.com.caelum.vraptor.ioc.cdi.extensions;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.BeforeBeanDiscovery;
import javax.enterprise.inject.spi.Extension;

import br.com.caelum.vraptor.ioc.cdi.CDIRegistry;

public class RegisterComponentsExtension implements Extension {

	public void beforeBeanDiscovey(@Observes BeforeBeanDiscovery discovery,
			BeanManager bm) {
		CDIRegistry registry = new CDIRegistry(discovery, bm);
		registry.configure();
	}
}
