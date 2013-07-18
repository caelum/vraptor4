package br.com.caelum.vraptor4.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor4.AfterCall;
import br.com.caelum.vraptor4.BeforeCall;
import br.com.caelum.vraptor4.VRaptorException;
import br.com.caelum.vraptor4.core.InterceptorHandler;
import br.com.caelum.vraptor4.core.InterceptorStack;
import br.com.caelum.vraptor4.ioc.Container;
import br.com.caelum.vraptor4.restfulie.controller.ControllerInstance;
import br.com.caelum.vraptor4.restfulie.controller.ControllerMethod;

public class AspectStyleInterceptorHandler implements InterceptorHandler {

	private StepInvoker stepInvoker;
	private Container container;
	private Class<?> interceptorClass;
	private static final Logger logger = LoggerFactory
			.getLogger(AspectStyleInterceptorHandler.class);
	private InterceptorMethodParametersResolver parametersResolver;
	private StepExecutor<Boolean> acceptsExecutor;
	private StepExecutor<?> after;
	private StepExecutor<?> around;
	private StepExecutor<?> before;


	public AspectStyleInterceptorHandler(Class<?> interceptorClass,
			StepInvoker stepInvoker, Container container) {
		this.interceptorClass = interceptorClass;
		this.stepInvoker = stepInvoker;
		this.container = container;
		parametersResolver = new InterceptorMethodParametersResolver(
				stepInvoker, container);
		configure();

	}

	private void configure() {		
		after = new NoStackParameterStepExecutor(stepInvoker, AfterCall.class);		
		if(!after.accept(interceptorClass)){
			after = new DoNothingStepExecutor();
		}
		
		around = new AroundExecutor(stepInvoker,parametersResolver, container);
		if(!around.accept(interceptorClass)){
			around = new StackNextExecutor(container);
		}
		
		before = new NoStackParameterStepExecutor(stepInvoker, BeforeCall.class);
		if(!before.accept(interceptorClass)){
			before = new DoNothingStepExecutor();
		}
		
		CustomAcceptsExecutor customAcceptsExecutor = new CustomAcceptsExecutor(stepInvoker, container);
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
			stack.next(controllerMethod, currentController);
		}

	}

}
