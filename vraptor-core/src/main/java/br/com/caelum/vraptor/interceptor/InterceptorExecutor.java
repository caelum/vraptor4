package br.com.caelum.vraptor.interceptor;

import java.lang.reflect.Method;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import br.com.caelum.vraptor.AfterCall;
import br.com.caelum.vraptor.AroundCall;
import br.com.caelum.vraptor.BeforeCall;

/**
 * Simple executor for interceptor methods containing annotations:
 * {@link BeforeCall}, {@link AroundCall} or {@link AfterCall}
 *
 * @author Rodrigo Turini
 * @since 4.0
 */
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

	/**
	 * @param interceptor to be executed
	 * @param method that should be annotated with {@link BeforeCall} or {@link AfterCall}
	 */
	public void execute(Object interceptor, Method method) {
		if (method != null) {
			stepInvoker.tryToInvoke(interceptor, method);
		}
	}

	/**
	 * <strong>note</strong>: Just for this case, method can receive DI.
	 * @param interceptor to be executed
	 * @param method that should be annotated with {@link AroundCall}.
	 */
	public void executeAround(Object interceptor, Method method) {
		if (method != null) {
			Object[] params = parametersResolver.parametersFor(method);
			stepInvoker.tryToInvoke(interceptor, method, params);
		} else {
			simpleInterceptorStack.get().next();
		}
	}
}