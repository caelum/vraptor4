package br.com.caelum.vraptor.interceptor;

import static br.com.caelum.vraptor.interceptor.CustomAcceptsVerifier.getCustomAcceptsAnnotations;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import br.com.caelum.vraptor.controller.ControllerInstance;
import br.com.caelum.vraptor.controller.ControllerMethod;

@ApplicationScoped
public class CustomAcceptsExecutor {

	private final Instance<ControllerMethod> controllerMethod;
	private final Instance<ControllerInstance> controllerInstance;
	private final StepInvoker invoker;
	private final CustomAcceptsVerifier acceptsVerifier;

	/**
	 * @deprecated CDI eyes only
	 */
	protected CustomAcceptsExecutor() {
		this(null, null, null, null);
	}

	@Inject
	public CustomAcceptsExecutor(Instance<ControllerMethod> controllerMethod,
			Instance<ControllerInstance> controllerInstance,
			StepInvoker invoker, CustomAcceptsVerifier acceptsVerifier) {

		this.controllerMethod = controllerMethod;
		this.controllerInstance = controllerInstance;
		this.invoker = invoker;
		this.acceptsVerifier = acceptsVerifier;
	}

	public boolean accepts(Object interceptor, Method method, List<Annotation> constraints) {
		if (constraints.isEmpty()) return false;
		boolean customAccepts = acceptsVerifier.isValid(interceptor,
				controllerMethod.get(), controllerInstance.get(), constraints);
		if (!customAccepts) invoker.tryToInvoke(interceptor, method);
		return customAccepts;
	}

	public List<Annotation> getCustomAccepts(Object interceptor) {
		return getCustomAcceptsAnnotations(interceptor.getClass());
	}
}