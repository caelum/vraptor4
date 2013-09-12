package br.com.caelum.vraptor.ioc.cdi;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Inject;

import br.com.caelum.vraptor.Controller;
import br.com.caelum.vraptor.Convert;
import br.com.caelum.vraptor.Intercepts;
import br.com.caelum.vraptor.controller.DefaultBeanClass;
import br.com.caelum.vraptor.core.ControllerQualifier;
import br.com.caelum.vraptor.core.ConvertQualifier;
import br.com.caelum.vraptor.core.DeserializesQualifier;
import br.com.caelum.vraptor.core.InterceptsQualifier;
import br.com.caelum.vraptor.core.StereotypeInfo;
import br.com.caelum.vraptor.deserialization.Deserializes;
import br.com.caelum.vraptor.deserialization.DeserializesHandler;
import br.com.caelum.vraptor.ioc.ControllerHandler;
import br.com.caelum.vraptor.ioc.ConverterHandler;
import br.com.caelum.vraptor.ioc.InterceptorStereotypeHandler;

@ApplicationScoped
public class StereotypesRegistry {


	private static final Map<Class<?>, StereotypeInfo> STEREOTYPES_INFO;
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
		Map<Class<?>, StereotypeInfo> stereotypesInfo = StereotypesRegistry.STEREOTYPES_INFO;
		for(Class<? extends Annotation> annotation : annotations){
			if(stereotypesInfo.containsKey(annotation)){
				return stereotypesInfo.get(annotation).getStereotypeQualifier();
			}
		}
		return null;
	}

    static {
    		HashMap<Class<?>, StereotypeInfo> map = new HashMap<Class<?>,StereotypeInfo>();
    		map.put(Controller.class,new StereotypeInfo(Controller.class,ControllerHandler.class,new AnnotationLiteral<ControllerQualifier>() {}));
    		map.put(Convert.class,new StereotypeInfo(Convert.class,ConverterHandler.class,new AnnotationLiteral<ConvertQualifier>() {}));
    		map.put(Deserializes.class,new StereotypeInfo(Deserializes.class,DeserializesHandler.class,new AnnotationLiteral<DeserializesQualifier>() {}));
    		map.put(Intercepts.class,new StereotypeInfo(Intercepts.class,InterceptorStereotypeHandler.class,new AnnotationLiteral<InterceptsQualifier>() {}));
    		STEREOTYPES_INFO = Collections.unmodifiableMap(map);
    }
}