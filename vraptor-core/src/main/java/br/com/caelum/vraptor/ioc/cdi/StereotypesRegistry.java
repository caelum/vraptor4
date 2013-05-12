package br.com.caelum.vraptor.ioc.cdi;

import java.util.ArrayList;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.spi.Bean;
import javax.inject.Inject;

import br.com.caelum.vraptor.ioc.StereotypeHandler;
import br.com.caelum.vraptor4.ioc.cdi.BeanManagerUtil;

@ApplicationScoped
public class StereotypesRegistry {

	private BeanManagerUtil beanManagerUtil;

	//CDI eyes only
	@Deprecated
	public StereotypesRegistry() {
	}
	
	@Inject
	public StereotypesRegistry(BeanManagerUtil beanManagerUtil) {
		this.beanManagerUtil = beanManagerUtil;
	}
	
	public void configure(){
		ArrayList<StereotypeHandler> stereotypesHandler = new ArrayList<StereotypeHandler>();		
		Set<Bean<?>> stereotypeBeans = beanManagerUtil.getBeans(StereotypeHandler.class);
		for (Bean<?> bean : stereotypeBeans) {
			StereotypeHandler stereotype = (StereotypeHandler) beanManagerUtil.instanceFor(bean);
			stereotypesHandler.add(stereotype);
		}

		Set<Bean<?>> beans = beanManagerUtil.getBeans(Object.class);		
		for (Bean<?> bean : beans) {
			for (StereotypeHandler handler : stereotypesHandler) {
				if (bean.getBeanClass().isAnnotationPresent(handler.stereotype())) {
					handler.handle(bean.getBeanClass());
				}
			}
		}		
	}
	
	
}
