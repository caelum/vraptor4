package br.com.caelum.vraptor.ioc.cdi;

import java.util.concurrent.Callable;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.CDI;
import javax.inject.Inject;

import br.com.caelum.vraptor.cache.CacheStore;
import br.com.caelum.vraptor.cache.LRU;
import br.com.caelum.vraptor.ioc.Container;

@ApplicationScoped
@SuppressWarnings("rawtypes")
public class CDIBasedContainer implements Container {

	private CacheStore<Class<?>,Instance> cache;

	@Deprecated
	public CDIBasedContainer() {
	}

	@Inject
	public CDIBasedContainer(@LRU(capacity=1000) CacheStore<Class<?>, Instance> cache) {
		super();
		this.cache = cache;
	}

	@Override
	public <T> T instanceFor(Class<T> type) {
		return selectFromContainer(type).get();
	}

	@Override
	public <T> boolean canProvide(Class<T> type) {
		return !selectFromContainer(type).isUnsatisfied();
	}

	@SuppressWarnings("unchecked")
	private <T> Instance<T> selectFromContainer(final Class<T> type) {
		return cache.fetch(type, new Callable<Instance>() {
			@Override
			public Instance call() throws Exception {
				return CDI.current().select(type);
			}
		});
	}
}