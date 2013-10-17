package br.com.caelum.vraptor.interceptor;

import static com.google.common.collect.Collections2.filter;
import static java.lang.String.format;
import static java.util.Arrays.asList;

import java.lang.reflect.Method;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;

import br.com.caelum.vraptor.AroundCall;
import br.com.caelum.vraptor.core.InterceptorStack;

import com.google.common.base.Predicate;

@ApplicationScoped
public class NoStackParamValidationRule implements ValidationRule {

	@Override
	public void validate(Class<?> originalType) {

		Method aroundCall = null;  // TODO handle method efficiently
		Method afterCall = null;   // TODO handle method efficiently
		Method beforeCall = null;  // TODO handle method efficiently
		Method accepts = null;     // TODO handle method efficiently

		if (!containsStack(aroundCall)) {
			invalidUseOfStack("@%s method must receive %s or %s");
		}
		if (containsStack(beforeCall) || containsStack(afterCall)
				|| containsStack(accepts)) {
			invalidUseOfStack("Non @%s method must not receive %s or %s");
		}
	}

	private void invalidUseOfStack(String message) {
		throw new IllegalArgumentException(format(message,
				AroundCall.class.getSimpleName(),
				InterceptorStack.class.getName(),
				SimpleInterceptorStack.class.getName()));
	}

	private boolean containsStack(Method method) {
		List<Class<?>> parameterTypes = asList(method.getParameterTypes());
		Predicate<Class<?>> hasStack = new Predicate<Class<?>>() {
			@Override
			public boolean apply(Class<?> input) {
				return SimpleInterceptorStack.class.isAssignableFrom(input)
					|| InterceptorStack.class.isAssignableFrom(input);
			}
		};
		return !filter(parameterTypes, hasStack).isEmpty();
	}
}