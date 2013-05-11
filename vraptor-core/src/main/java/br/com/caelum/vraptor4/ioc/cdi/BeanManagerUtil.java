package br.com.caelum.vraptor4.ioc.cdi;

import java.lang.annotation.Annotation;
import java.util.Set;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

public class BeanManagerUtil {

	private BeanManager beanManager;
	
	//CDI eyes only
	@Deprecated
	public BeanManagerUtil() {
	}
	
	@Inject
	public BeanManagerUtil(BeanManager beanManager) {
		this.beanManager = beanManager;
	}

	public <T> T instanceFor(Class<T> type) {
		Set beans = getBeans(type);
		Bean<T> bean = beanManager.resolve(beans);
		return instanceFor(bean,type);
	}
	
	public <T> T instanceFor(Bean<?> bean){		
		return instanceFor(bean,bean.getBeanClass());		
	}
	
	public <T> T instanceFor(Bean<?> bean,Class<?> specificType){
		CreationalContext ctx = beanManager.createCreationalContext(bean);
		return (T) beanManager.getReference(bean, specificType, ctx);		
	}	
	
	public Set<Bean<?>> getBeans(Class<?> type){
		return beanManager.getBeans(type);
	}

	public Set<Bean<?>> getBeans(Class<?> type,Annotation... qualifiers) {
		return beanManager.getBeans(type,qualifiers);
	}
	
	public BeanManager getBeanManager() {
		return beanManager;
	}
	
}