package br.com.caelum.vraptor.ioc.cdi;

import javax.enterprise.inject.spi.CDI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor.ComponentRegistry;
import br.com.caelum.vraptor.ioc.Container;

public class CDIBasedContainer implements Container, ComponentRegistry {

	private static final Logger logger = LoggerFactory.getLogger(CDIBasedContainer.class);

	//CDI eyes only
	@Deprecated
	public CDIBasedContainer() {
	}

	@Override
	public <T> T instanceFor(Class<T> type) {
		return CDI.current().select(type).get();
	}

	@Override
	public <T> boolean canProvide(Class<T> type) {
		return !CDI.current().select(type).isUnsatisfied();
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
