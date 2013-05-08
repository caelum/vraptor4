package br.com.caelum.vraptor.ioc.cdi;

import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor.ComponentRegistry;
import br.com.caelum.vraptor.ioc.Container;

public class CDIBasedContainer implements Container, ComponentRegistry {

	private final BeanManager beanManager;
	private static final Logger logger = LoggerFactory
			.getLogger(CDIBasedContainer.class);
	private final BeanManagerUtil beanManagerUtil;

	@Inject
	public CDIBasedContainer(BeanManager beanManager) {
		this.beanManager = beanManager;
		this.beanManagerUtil = new BeanManagerUtil(beanManager);
	}

	public <T> T instanceFor(Class<T> type) {
		return this.beanManagerUtil.instanceFor(type);
	}

	public <T> boolean canProvide(Class<T> type) {
		return !beanManager.getBeans(type).isEmpty();
	}

	public void register(Class<?> requiredType, Class<?> componentType) {
		// it is not possible using CDI. We can only registrer on the container
		// startup.
		logger.debug("Should register " + requiredType + " associated with "
				+ componentType);
	}

	public void deepRegister(Class<?> componentType) {
		logger.debug("Should deep register " + componentType);
	}

}
