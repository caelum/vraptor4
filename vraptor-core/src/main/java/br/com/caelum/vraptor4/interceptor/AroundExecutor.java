package br.com.caelum.vraptor4.interceptor;

import java.lang.reflect.Method;

import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.ioc.Container;
import br.com.caelum.vraptor4.AroundCall;
import br.com.caelum.vraptor4.controller.ControllerInstance;
import br.com.caelum.vraptor4.controller.ControllerMethod;

public class AroundExecutor implements StepExecutor<Object> {

	private StepInvoker stepInvoker;
	private InterceptorMethodParametersResolver parametersResolver;
	private Container container;

	public AroundExecutor(StepInvoker stepInvoker,
			InterceptorMethodParametersResolver parametersResolver,
			Container container) {
		super();
		this.stepInvoker = stepInvoker;
		this.parametersResolver = parametersResolver;
		this.container = container;
	}

	public boolean accept(Class<?> interceptorClass) {
		Method around = stepInvoker.findMethod(AroundCall.class,
				interceptorClass);
		if (around != null) {
			MustReceiveStackAsParameterAcceptor stackAcceptor = new MustReceiveStackAsParameterAcceptor();
			if (!stackAcceptor.accepts(around)) {
				throw new IllegalArgumentException(stackAcceptor.errorMessage());
			}
			return true;
		}
		return false;
	}

	public Object execute(Object interceptor) {
		return stepInvoker
				.tryToInvoke(interceptor, AroundCall.class, parametersResolver
						.parametersFor(AroundCall.class, interceptor));
	}
}
