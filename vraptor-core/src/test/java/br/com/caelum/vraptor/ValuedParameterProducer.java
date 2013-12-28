package br.com.caelum.vraptor;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;

import br.com.caelum.vraptor.cache.CacheStore;
import br.com.caelum.vraptor.cache.DefaultCacheStore;
import br.com.caelum.vraptor.http.Parameter;
import br.com.caelum.vraptor.http.ParameterNameProvider;
import br.com.caelum.vraptor.http.ParanamerNameProvider;
import br.com.caelum.vraptor.http.ValuedParameter;

public class ValuedParameterProducer {

	private static final CacheStore<AccessibleObject, Parameter[]> cache = new DefaultCacheStore<>();
	private static final ParameterNameProvider parameterNameProvider = new ParanamerNameProvider(cache);

	public static ValuedParameter[] from(Method method, Object[] values) {
		Parameter[] parameters = parameterNameProvider.parametersFor(method);
		ValuedParameter[] valuedParameters = new ValuedParameter[parameters.length];

		for (int i = 0; i < parameters.length; i++) {
			Object value = i < values.length ? values[i] : null;
			valuedParameters[i] = new ValuedParameter(parameters[i], value);
		}

		return valuedParameters;
	}

	public static ValuedParameter[] from(Method method) {
		return from(method, new Object[0]);
	}
}
