package br.com.caelum.vraptor.ioc.cdi.extension;

import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterDeploymentValidation;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.util.AnnotationLiteral;

import br.com.caelum.vraptor.ioc.cdi.EagerInitialized;

/**
 * When enabled, it allows users to use {@link EagerInitialized} annotation 
 * to get your application scoped beans initialized just after CDI deployment.
 *
 * @author Rodrigo Turini
 */
public class EagerApplicationScopedExtension implements Extension {
	
	@SuppressWarnings("serial")
	public void initialize(@Observes AfterDeploymentValidation event, BeanManager beanManager) {
		
		Set<Bean<?>> beans = beanManager.getBeans(Object.class, 
				new AnnotationLiteral<EagerInitialized>() {});
		
		for (Bean<?> bean : beans) {
			Class<?> beanClass = bean.getBeanClass();
			if(beanClass.isAnnotationPresent(ApplicationScoped.class)){
				CreationalContext<?> ctx = beanManager.createCreationalContext(bean);
				beanManager.getReference(bean, beanClass, ctx).toString();
			}
		}
	}
}
