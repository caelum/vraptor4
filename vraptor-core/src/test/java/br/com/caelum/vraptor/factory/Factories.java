package br.com.caelum.vraptor.factory;

import java.lang.reflect.AccessibleObject;

import br.com.caelum.vraptor.cache.CacheStore;
import br.com.caelum.vraptor.cache.DefaultCacheStore;
import br.com.caelum.vraptor.http.Parameter;
import br.com.caelum.vraptor.http.ParameterNameProvider;
import br.com.caelum.vraptor.http.ParanamerNameProvider;

public class Factories {

	public static ParameterNameProvider createParameterNameProvider() {
		CacheStore<AccessibleObject, Parameter[]> cache = new DefaultCacheStore<>();
		return new ParanamerNameProvider(cache);
	}
}
