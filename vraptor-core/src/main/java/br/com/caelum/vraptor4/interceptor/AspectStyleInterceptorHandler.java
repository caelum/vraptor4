package br.com.caelum.vraptor4.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor.VRaptorException;
import br.com.caelum.vraptor.core.InterceptorHandler;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.ioc.Container;
import br.com.caelum.vraptor4.AfterCall;
import br.com.caelum.vraptor4.BeforeCall;
import br.com.caelum.vraptor4.controller.ControllerInstance;
import br.com.caelum.vraptor4.controller.ControllerMethod;

public class AspectStyleInterceptorHandler implements InterceptorHandler {

	private StepInvoker stepInvoker;
	private Container container;
	private Class<?> interceptorClass;
	private static final Logger logger = LoggerFactory
			.getLogger(AspectStyleInterceptorHandler.class);
	private InterceptorMethodParametersResolver parametersResolver;
	private ControllerInstance controllerInstance;
	private ControllerMethod controllerMethod;
	private InterceptorStack interceptorStack;
	private StepExecutor<Boolean> acceptsExecutor;
	private NoStackParameterStepExecutor after;
	private AroundExecutor around;
	private NoStackParameterStepExecutor before;


	public AspectStyleInterceptorHandler(Class<?> interceptorClass,
			StepInvoker stepInvoker, Container container) {
		this.interceptorClass = interceptorClass;
		this.stepInvoker = stepInvoker;
		this.container = container;
		parametersResolver = new InterceptorMethodParametersResolver(
				stepInvoker, container);
		this.controllerInstance = container.instanceFor(ControllerInstance.class);
		this.controllerMethod = container.instanceFor(ControllerMethod.class);
		this.interceptorStack = container.instanceFor(InterceptorStack.class);
		configure();

	}

	private void configure() {		
		after = new NoStackParameterStepExecutor(stepInvoker, AfterCall.class);		
		after.accept(interceptorClass);
		
		around = new AroundExecutor(stepInvoker, interceptorStack, parametersResolver,controllerMethod,controllerInstance);
		around.accept(interceptorClass);
		
		before = new NoStackParameterStepExecutor(stepInvoker, BeforeCall.class);
		before.accept(interceptorClass);
		
		StepExecutor<Boolean> customAcceptsExecutor = new CustomAcceptsExecutor(stepInvoker, container, controllerMethod, controllerInstance);
		InterceptorAcceptsExecutor interceptorAcceptsExecutor = new InterceptorAcceptsExecutor(stepInvoker, parametersResolver);		
		boolean customAccepts = customAcceptsExecutor.accept(interceptorClass);
		boolean internalAccepts = interceptorAcceptsExecutor.accept(interceptorClass);
		if(customAccepts && internalAccepts){
			throw new VRaptorException("Interceptor "+interceptorClass+" must declare internal accepts or custom, not both.");
		}
		
		this.acceptsExecutor = customAccepts?customAcceptsExecutor:interceptorAcceptsExecutor;
		
		
	

	}

	public void execute(InterceptorStack stack,
			ControllerMethod controllerMethod, Object currentController) {
		Object interceptor = container.instanceFor(interceptorClass);

		logger.debug("Invoking interceptor {}", interceptor.getClass()
				.getSimpleName());

		if (acceptsExecutor.execute(interceptor)) {
			before.execute(interceptor);
			around.execute(interceptor);
			after.execute(interceptor);
		} else {
			stack.next(controllerMethod, controllerInstance.getController());
		}

	}

}
