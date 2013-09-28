package br.com.caelum.vraptor.ioc.cdi;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.CDI;
import javax.inject.Inject;

import br.com.caelum.vraptor.cache.LRU;
import br.com.caelum.vraptor.cache.VRaptorCache;
import br.com.caelum.vraptor.ioc.Container;

@ApplicationScoped
@SuppressWarnings("rawtypes")
public class CDIBasedContainer implements Container {
	
	private VRaptorCache<String,Instance> cache;

	@Deprecated
	public CDIBasedContainer() {
	}
	
	@Inject
	public CDIBasedContainer(@LRU(capacity=1000) VRaptorCache<String, Instance> cache) {
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
		String className = type.getCanonicalName();
		if(cache.get(className)==null){
			Instance<T> instance = CDI.current().select(type);
			cache.put(className,instance);
		}
		return cache.get(className);
	}
}