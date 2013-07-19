package br.com.caelum.vraptor4.ioc.cdi;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

import br.com.caelum.vraptor4.core.BaseComponents;
import br.com.caelum.vraptor4.core.StereotypeInfo;
import br.com.caelum.vraptor4.restfulie.controller.DefaultBeanClass;

@ApplicationScoped
public class StereotypesRegistry {
	
	@Inject private BeanManager beanManager;

	public void configure(){
		Set<Bean<?>> beans = beanManager.getBeans(Object.class);
		for (Bean<?> bean : beans) {
			Annotation qualifier = tryToFindAStereotypeQualifier(bean);
			if(qualifier!=null){
				beanManager.fireEvent(new DefaultBeanClass(bean.getBeanClass()),qualifier);
			}
		}
	}

	private Annotation tryToFindAStereotypeQualifier(Bean<?> bean) {
		Set<Class<? extends Annotation>> annotations = bean.getStereotypes();
		Map<Class<? extends Annotation>, StereotypeInfo> stereotypesInfo = BaseComponents.getStereotypesInfoMap();
		for(Class<? extends Annotation> annotation : annotations){
			if(stereotypesInfo.containsKey(annotation)){
				return stereotypesInfo.get(annotation).getStereotypeQualifier();
			}
		}
		return null;
	}
}
