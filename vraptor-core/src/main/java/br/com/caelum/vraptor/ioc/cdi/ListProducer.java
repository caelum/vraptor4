package br.com.caelum.vraptor.ioc.cdi;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;

import br.com.caelum.vraptor.ioc.cdi.BeanManagerUtil;

public class ListProducer {
	
	private final BeanManager beanManager;
	private final BeanManagerUtil beanManagerUtil;
	
	@Inject
	public ListProducer(BeanManager beanManager) {
		this.beanManager = beanManager;
		beanManagerUtil = new BeanManagerUtil(beanManager);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Produces
	public <T> List<T> producesList(InjectionPoint injectionPoint){
		ParameterizedType type = (ParameterizedType) injectionPoint.getType();
	    Class klass = (Class) type.getActualTypeArguments()[0];
	    Set<Bean<?>> beans = beanManager.getBeans(klass);
	    ArrayList objects = new ArrayList();
	    for (Bean<?> bean : beans) {			
			objects.add(beanManagerUtil.instanceFor(bean));
		}
		return objects;
	}
}
