package br.com.caelum.vraptor.ioc.cdi;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor.ComponentRegistry;
import br.com.caelum.vraptor.ioc.Container;
import br.com.caelum.vraptor4.ioc.cdi.BeanManagerUtil;

public class CDIBasedContainer implements Container, ComponentRegistry {

	private static final Logger logger = LoggerFactory.getLogger(CDIBasedContainer.class);
	private BeanManagerUtil beanManagerUtil;
	
	//CDI eyes only
	@Deprecated
	public CDIBasedContainer() {
	}

	@Inject
	public CDIBasedContainer(BeanManagerUtil beanManagerUtil) {
		this.beanManagerUtil = beanManagerUtil;
	}

	@Override
	public <T> T instanceFor(Class<T> type) {
		return this.beanManagerUtil.instanceFor(type);
	}

	@Override
	public <T> boolean canProvide(Class<T> type) {
		return !beanManagerUtil.getBeans(type).isEmpty();
	}

	@Override
	public void register(Class<?> requiredType, Class<?> componentType) {
		// it is not possible using CDI. We can only registrer on the container
		// startup.
		logger.debug("Should register " + requiredType + " associated with "
				+ componentType);
	}

	@Override
	public void deepRegister(Class<?> componentType) {
		logger.debug("Should deep register " + componentType);
	}

}
