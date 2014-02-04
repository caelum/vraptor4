package br.com.caelum.vraptor.ioc.cdi;

import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor.ioc.Container;
import br.com.caelum.vraptor.proxy.CDIProxies;

@ApplicationScoped
public class CDIBasedContainer implements Container {

	private static final Logger logger = LoggerFactory.getLogger(CDIBasedContainer.class);

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

	@Override
	@SuppressWarnings("unchecked")
	public <T> T instanceFor(Class<T> type) {
		type = (Class<T>) CDIProxies.extractRawTypeIfPossible(type);
		logger.debug("asking cdi to get instance for {}", type);
		
		Bean<?> bean = getBeanFrom(type);
		CreationalContext<?> ctx = beanManager.createCreationalContext(bean);
		return (T) beanManager.getReference(bean, type, ctx);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> boolean canProvide(Class<T> type) {
		type = (Class<T>) CDIProxies.extractRawTypeIfPossible(type);
		logger.debug("asking cdi to get instance for {}", type);
		
		return getBeanFrom(type) != null;
	}

	private <T> Bean<?> getBeanFrom(Class<T> type) {
		Set<Bean<?>> beans = beanManager.getBeans(type);
		logger.debug("beans for {} is {}", type, beans);

		return beanManager.resolve(beans);
	}
}