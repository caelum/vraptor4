package br.com.caelum.vraptor4.interceptor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;

import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.ioc.Container;
import br.com.caelum.vraptor4.AroundCall;
import br.com.caelum.vraptor4.controller.ControllerInstance;
import br.com.caelum.vraptor4.controller.ControllerMethod;

public class AroundExecutor {

	private StepInvoker stepInvoker;
	private InterceptorStack stack;
	private Container container;
	private InterceptorMethodParameterResolver parameterResolver;

	public AroundExecutor(StepInvoker stepInvoker, InterceptorStack stack,
			Container container) {
		super();
		this.stepInvoker = stepInvoker;
		this.stack = stack;
		this.container = container;
		this.parameterResolver = new InterceptorMethodParameterResolver(
				stepInvoker, container);
	}

	public void execute(Object interceptor, ControllerMethod controllerMethod,
			ControllerInstance controllerInstance) {
		if (noAround(interceptor)) {
			stack.next(controllerMethod, controllerInstance.getController());
		} else {
			stepInvoker.tryToInvoke(interceptor, AroundCall.class,
					new AroundSignatureAcceptor(), parameterResolver
							.parametersFor(AroundCall.class, interceptor));
		}
	}

	private boolean noAround(Object interceptor) {
		return stepInvoker.findMethod(AroundCall.class, interceptor) == null;
	}
}
