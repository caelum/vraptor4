package br.com.caelum.vraptor.ioc.cdi;

import java.util.ArrayList;
import java.util.Set;

import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;

import br.com.caelum.vraptor.ioc.StereotypeHandler;

public class StereotypesRegistry {

	private final BeanManagerUtil beanManagerUtil;

	public StereotypesRegistry(BeanManager bm) {
		beanManagerUtil = new BeanManagerUtil(bm);
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
