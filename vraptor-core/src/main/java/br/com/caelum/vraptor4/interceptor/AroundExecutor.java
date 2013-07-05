package br.com.caelum.vraptor4.interceptor;

import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.ioc.Container;
import br.com.caelum.vraptor4.AroundCall;
import br.com.caelum.vraptor4.controller.ControllerInstance;
import br.com.caelum.vraptor4.controller.ControllerMethod;

public class AroundExecutor {

	private StepInvoker stepInvoker;
	private InterceptorStack stack;
	private InterceptorMethodParametersResolver parametersResolver;

	public AroundExecutor(StepInvoker stepInvoker, InterceptorStack stack,InterceptorMethodParametersResolver parametersResolver) {
		super();
		this.stepInvoker = stepInvoker;
		this.stack = stack;
		this.parametersResolver = parametersResolver;
	}

	public void execute(Object interceptor, ControllerMethod controllerMethod,
			ControllerInstance controllerInstance) {
		if (noAround(interceptor)) {
			stack.next(controllerMethod, controllerInstance.getController());
		} else {
			stepInvoker.tryToInvoke(interceptor, AroundCall.class,
					new AroundSignatureAcceptor(), parametersResolver
							.parametersFor(AroundCall.class, interceptor));
		}
	}

	private boolean noAround(Object interceptor) {
		return stepInvoker.findMethod(AroundCall.class, interceptor) == null;
	}
}
