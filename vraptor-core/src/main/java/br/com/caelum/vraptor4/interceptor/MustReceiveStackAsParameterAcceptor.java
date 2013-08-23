package br.com.caelum.vraptor4.interceptor;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import br.com.caelum.vraptor4.AroundCall;
import br.com.caelum.vraptor4.core.InterceptorStack;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;

public class MustReceiveStackAsParameterAcceptor implements SignatureAcceptor {

	@Override
	public boolean accepts(Method method){
		List<Class<?>> parameterTypes = Arrays.asList(method.getParameterTypes());
		Collection<Class<?>> possibleStackParams = Collections2.filter(parameterTypes,new Predicate<Class<?>>() {

			@Override
			public boolean apply(Class<?> input) {
				return SimpleInterceptorStack.class.isAssignableFrom(input) || InterceptorStack.class.isAssignableFrom(input);
			}
		});
		return !possibleStackParams.isEmpty();
	}
	
	@Override
	public String errorMessage() {
		return AroundCall.class.getSimpleName()+" method must receive "+InterceptorStack.class.getName()+" or "+SimpleInterceptorStack.class.getName();
	}
}
