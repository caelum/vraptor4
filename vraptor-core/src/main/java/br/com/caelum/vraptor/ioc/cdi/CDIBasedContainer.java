package br.com.caelum.vraptor.ioc.cdi;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.CDI;
import javax.inject.Inject;

import br.com.caelum.vraptor.cache.LRU;
import br.com.caelum.vraptor.cache.Cache;
import br.com.caelum.vraptor.ioc.Container;

@ApplicationScoped
@SuppressWarnings("rawtypes")
public class CDIBasedContainer implements Container {
	
	private Cache<Class<?>,Instance> cache;

	@Deprecated
	public CDIBasedContainer() {
	}
	
	@Inject
	public CDIBasedContainer(@LRU(capacity=1000) Cache<Class<?>, Instance> cache) {
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

	private <T> Instance<T> selectFromContainer(Class<T> type) {
		Instance<T> instance = cache.get(type);
		if(instance == null){
			 instance = CDI.current().select(type);
			cache.put(type,instance);
		}
		return instance;
	}
}