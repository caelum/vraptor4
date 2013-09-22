package br.com.caelum.vraptor.ioc.cdi;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.CDI;

import br.com.caelum.vraptor.ioc.Container;

@ApplicationScoped
public class CDIBasedContainer implements Container {

	@Override
	public <T> T instanceFor(Class<T> type) {
		return selectFromContainer(type).get();
	}

	@Override
	public <T> boolean canProvide(Class<T> type) {
		return !selectFromContainer(type).isUnsatisfied();
	}

	private <T> Instance<T> selectFromContainer(Class<T> type) {
		return CDI.current().select(type);
	}
}