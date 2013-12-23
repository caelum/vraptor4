package br.com.caelum.vraptor.ioc.cdi;

import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

import br.com.caelum.vraptor.ioc.Container;

@ApplicationScoped
public class CDIBasedContainer implements Container {

	private final BeanManager beanManager;

	/**
	 * @deprecated CDI eyes only
	 */
	protected CDIBasedContainer(){
		this(null);
	}

	@Inject
	public CDIBasedContainer(BeanManager beanManager) {
		this.beanManager = beanManager;
	}

	@Override @SuppressWarnings("unchecked")
	public <T> T instanceFor(Class<T> type) {
		Bean<?> bean = getBeanFrom(type);
		CreationalContext<?> ctx = beanManager.createCreationalContext(bean);
		return (T) beanManager.getReference(bean, type, ctx);
	}

	@Override
	public <T> boolean canProvide(Class<T> type) {
		return getBeanFrom(type) != null;
	}

	private <T> Bean<?> getBeanFrom(Class<T> type) {
		Set<Bean<?>> beans = beanManager.getBeans(type);
		if (beans.isEmpty()) return null;
		return beanManager.resolve(beans);
	}
}