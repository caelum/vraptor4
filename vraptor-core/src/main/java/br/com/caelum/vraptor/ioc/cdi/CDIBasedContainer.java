package br.com.caelum.vraptor.ioc.cdi;

import javax.enterprise.inject.spi.CDI;

import br.com.caelum.vraptor.ioc.Container;

public class CDIBasedContainer implements Container {

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

}