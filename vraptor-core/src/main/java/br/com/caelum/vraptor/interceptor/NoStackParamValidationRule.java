package br.com.caelum.vraptor.interceptor;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Collections2.filter;
import static java.util.Arrays.asList;

import java.lang.reflect.Method;
import java.util.List;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import net.vidageek.mirror.list.dsl.MirrorList;
import br.com.caelum.vraptor.Accepts;
import br.com.caelum.vraptor.AfterCall;
import br.com.caelum.vraptor.AroundCall;
import br.com.caelum.vraptor.BeforeCall;
import br.com.caelum.vraptor.core.InterceptorStack;

import com.google.common.base.Predicate;

@Dependent
public class NoStackParamValidationRule implements ValidationRule {

	private final StepInvoker invoker;
	
	/** @deprecated CDI eyes only */
	protected NoStackParamValidationRule() {
		this(null);
	}

	@Inject
	public NoStackParamValidationRule(StepInvoker invoker) {
		this.invoker = invoker;
	}

	@Override
	public void validate(Class<?> originalType, MirrorList<Method> methods) {

		Method aroundCall = invoker.findMethod(methods, AroundCall.class, originalType);
		Method afterCall = invoker.findMethod(methods, AfterCall.class, originalType);
		Method beforeCall = invoker.findMethod(methods, BeforeCall.class, originalType);
		Method accepts = invoker.findMethod(methods, Accepts.class, originalType);

		String interceptorStack = InterceptorStack.class.getName();
		String simpleInterceptorStack = SimpleInterceptorStack.class.getName();

		checkState(aroundCall == null || containsStack(aroundCall), "@AroundCall method must receive %s or %s",
				interceptorStack, simpleInterceptorStack);

		checkState(!containsStack(beforeCall) && !containsStack(afterCall) && !containsStack(accepts),
				"Non @AroundCall method must not receive %s or %s", interceptorStack, simpleInterceptorStack);
	}

	private boolean containsStack(Method method) {
		if (method == null) return false;

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