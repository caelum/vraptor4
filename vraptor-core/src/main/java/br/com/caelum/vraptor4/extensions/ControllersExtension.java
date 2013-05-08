package br.com.caelum.vraptor4.extensions;

import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessAnnotatedType;

import br.com.caelum.vraptor4.Controller;
import br.com.caelum.vraptor4.others.ScannedControllers;

public class ControllersExtension implements Extension{

	ScannedControllers controllers = new ScannedControllers();

	public <T> void scanControllers(@Observes ProcessAnnotatedType<T> pat, BeanManager beanManager) {
		Class<T> clazz = pat.getAnnotatedType().getJavaClass();

		if (Modifier.isAbstract(clazz.getModifiers())) return;

		for (Annotation annotation : clazz.getAnnotations()) {
			if (annotation.annotationType() == Controller.class 
					|| annotation.annotationType().isAnnotationPresent(Controller.class)) {
				controllers.add(clazz);
			}
		}
	}
	
	
}
