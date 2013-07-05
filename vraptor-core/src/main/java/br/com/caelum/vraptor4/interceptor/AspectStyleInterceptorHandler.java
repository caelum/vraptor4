package br.com.caelum.vraptor4.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor.core.InterceptorHandler;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.ioc.Container;
import br.com.caelum.vraptor4.AfterCall;
import br.com.caelum.vraptor4.BeforeCall;
import br.com.caelum.vraptor4.controller.ControllerInstance;
import br.com.caelum.vraptor4.controller.ControllerMethod;
import br.com.caelum.vraptor4.controller.DefaultControllerInstance;

public class AspectStyleInterceptorHandler implements InterceptorHandler{

	private StepInvoker stepInvoker;
	private Container container;
	private Class<?> interceptorClass;
	private static final Logger logger = LoggerFactory.getLogger(AspectStyleInterceptorHandler.class);

	public AspectStyleInterceptorHandler(Class<?> interceptorClass, StepInvoker stepInvoker,Container container) {
		this.interceptorClass = interceptorClass;
		this.stepInvoker = stepInvoker;
		this.container = container;

	}
	
	public void execute(InterceptorStack stack,ControllerMethod controllerMethod,Object currentController) {		
		Object interceptor = container.instanceFor(interceptorClass);
		ControllerInstance controllerInstance = new DefaultControllerInstance(currentController);
		InterceptorContainerDecorator interceptorContainer = new InterceptorContainerDecorator(container,stack,controllerMethod,controllerInstance,new DefaultSimpleInterceptorStack(stack, controllerMethod, controllerInstance));
		InterceptorMethodParametersResolver parametersResolver = new InterceptorMethodParametersResolver(stepInvoker, interceptorContainer);
		
		logger.debug("Invoking interceptor {}", interceptor.getClass().getSimpleName());
		
		boolean customAccepts = new CustomAcceptsExecutor(stepInvoker, container).execute(interceptor, controllerMethod, controllerInstance);
		boolean interceptorAccepts = new InterceptorAcceptsExecutor(stepInvoker,parametersResolver).execute(interceptor);
		if(customAccepts && interceptorAccepts){		
			stepInvoker.tryToInvoke(interceptor,BeforeCall.class,new NoStackParameterSignatureAcceptor());			
			new AroundExecutor(stepInvoker, stack, parametersResolver).execute(interceptor, controllerMethod, controllerInstance);
			stepInvoker.tryToInvoke(interceptor,AfterCall.class,new NoStackParameterSignatureAcceptor());
		} else {
			stack.next(controllerMethod, controllerInstance.getController());
		}

	}




}
