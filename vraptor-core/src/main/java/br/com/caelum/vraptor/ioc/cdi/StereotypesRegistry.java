package br.com.caelum.vraptor.ioc.cdi;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.spi.Bean;
import javax.inject.Inject;

import br.com.caelum.vraptor4.ioc.cdi.BeanManagerUtil;
import br.com.caelum.vraptor.core.BaseComponents;
import br.com.caelum.vraptor.core.StereotypeInfo;
import br.com.caelum.vraptor4.controller.DefaultBeanClass;

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
		Set<Bean<?>> beans = beanManagerUtil.getBeans(Object.class);		
		for (Bean<?> bean : beans) {
			Annotation qualifier = tryToFindAStereotypeQualifier(bean);
			if(qualifier!=null){
				beanManagerUtil.getBeanManager().fireEvent(new DefaultBeanClass(bean.getBeanClass()),qualifier);
			}
		}
	}

	private Annotation tryToFindAStereotypeQualifier(Bean<?> bean) {
		Annotation[] annotations = bean.getBeanClass().getAnnotations();
		Map<Class<? extends Annotation>, StereotypeInfo> stereotypesInfo = BaseComponents.getStereotypesInfoMap();
		for(Annotation annotation : annotations){
			if(stereotypesInfo.containsKey(annotation.annotationType())){
				return stereotypesInfo.get(annotation.annotationType()).getStereotypeQualifier();
			}
		}
		return null;
	}
	
	
	
	
}
