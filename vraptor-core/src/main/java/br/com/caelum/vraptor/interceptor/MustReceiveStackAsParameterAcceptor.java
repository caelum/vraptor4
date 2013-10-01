package br.com.caelum.vraptor.interceptor;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.enterprise.inject.Vetoed;

import br.com.caelum.vraptor.AroundCall;
import br.com.caelum.vraptor.core.InterceptorStack;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;

@Vetoed
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
