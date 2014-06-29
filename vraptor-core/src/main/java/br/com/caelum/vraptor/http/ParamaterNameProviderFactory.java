package br.com.caelum.vraptor.http;

import java.lang.reflect.AccessibleObject;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import br.com.caelum.vraptor.cache.CacheStore;

@Dependent
public class ParamaterNameProviderFactory {

	private final CacheStore<AccessibleObject, Parameter[]> cache;

	protected ParamaterNameProviderFactory() {
		this(null);
	}

	@Inject
	public ParamaterNameProviderFactory(CacheStore<AccessibleObject, Parameter[]> cache) {
		this.cache = cache;
	}

	@ApplicationScoped
	@Produces
	public ParameterNameProvider instance() {
		return isJava8() ? new JavaParameterNameProvider(cache) : new ParanamerNameProvider();
	}

	private boolean isJava8() {
		return System.getProperty("java.vm.specification.version").equals("1.8");
	}
}