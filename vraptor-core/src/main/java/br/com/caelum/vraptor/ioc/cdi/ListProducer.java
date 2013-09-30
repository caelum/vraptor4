package br.com.caelum.vraptor.ioc.cdi;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.CDI;
import javax.enterprise.inject.spi.InjectionPoint;

/**
 * @deprecated This class will be deleted very soon
 */
public class ListProducer {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Produces
	public <T> List<T> producesList(InjectionPoint injectionPoint){
		ParameterizedType type = (ParameterizedType) injectionPoint.getType();
		Class klass = (Class) type.getActualTypeArguments()[0];
		CDI<Object> currentCDI = CDI.current();
		BeanManager beanManager = currentCDI.getBeanManager();
		Set<Bean<?>> beans = beanManager.getBeans(klass);
		ArrayList objects = new ArrayList();
		for (Bean<?> bean : beans) {
			objects.add(currentCDI.select(bean.getBeanClass()).get());
		}
		return objects;
	}
}