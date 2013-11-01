package br.com.caelum.vraptor.interceptor;

import java.lang.reflect.Method;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

@ApplicationScoped
public class InterceptorExecutor {

	private final StepInvoker stepInvoker;
	private final InterceptorMethodParametersResolver parametersResolver;
	private final Instance<SimpleInterceptorStack> simpleInterceptorStack;

	/**
	 * @deprecated CDI eyes only
	 */
	protected InterceptorExecutor() {
		this(null, null, null);
	}

	@Inject
	public InterceptorExecutor(StepInvoker stepInvoker, InterceptorMethodParametersResolver parametersResolver,
			Instance<SimpleInterceptorStack> simpleInterceptorStack) {
		this.stepInvoker = stepInvoker;
		this.parametersResolver = parametersResolver;
		this.simpleInterceptorStack = simpleInterceptorStack;
	}

	public void execute(Object interceptor, Method method) {
		if (method != null) {
			executeMethod(interceptor, method);
		}
	}

	public void executeAround(Object interceptor, Method method) {
		if (method != null) {
			executeMethod(interceptor, method);
		} else {
			simpleInterceptorStack.get().next();
		}
	}

	private void executeMethod(Object interceptor, Method method) {
		Object[] params = parametersResolver.parametersFor(method);
		stepInvoker.tryToInvoke(interceptor, method, params);
	}
}