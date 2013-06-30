package br.com.caelum.vraptor4.interceptor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;

import javax.interceptor.AroundInvoke;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor.core.InterceptorHandler;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.ioc.Container;
import br.com.caelum.vraptor4.Accepts;
import br.com.caelum.vraptor4.AfterCall;
import br.com.caelum.vraptor4.AroundCall;
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
		InterceptorStackDecorator interceptorStackDecorator = new InterceptorStackDecorator(stack);
		InterceptorContainerDecorator interceptorContainer = new InterceptorContainerDecorator(container,interceptorStackDecorator,controllerMethod,controllerInstance,new DefaultSimpleInterceptorStack(interceptorStackDecorator, controllerMethod, controllerInstance));
		boolean accepts = new CustomiAcceptsVerifier(controllerMethod,controllerInstance,container,interceptor).isValid();		
		Object returnObject = stepInvoker.tryToInvoke(interceptor,Accepts.class,new BeforeAfterSignatureAcceptor(),parametersFor(Accepts.class,interceptor,interceptorContainer));		
		if(returnObject!=null){			
			if(!returnObject.getClass().equals(Boolean.class)){
				throw new IllegalStateException("@Accepts method should return boolean");
			}
			accepts = (Boolean) returnObject;
		}			
		if(accepts){		
			logger.debug("Invoking interceptor {}", interceptor.getClass().getSimpleName());
			stepInvoker.tryToInvoke(interceptor,BeforeCall.class,new BeforeAfterSignatureAcceptor());			
			if(noAround(interceptor) && !interceptorStackDecorator.isNexted()){
				stack.next(controllerMethod,controllerInstance.getController());
			} else{				
			   stepInvoker.tryToInvoke(interceptor,
					   AroundCall.class,
					   new AroundSignatureAcceptor(),
					   parametersFor(AroundCall.class,interceptor,interceptorContainer)
					   );
			}
			stepInvoker.tryToInvoke(interceptor,AfterCall.class,new BeforeAfterSignatureAcceptor());
		} else {
			stack.next(controllerMethod, controllerInstance.getController());
		}

	}
	
	private boolean noAround(Object interceptor) {
		return stepInvoker.findMethod(AroundCall.class, interceptor) == null;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Object[] parametersFor(Class<? extends Annotation> step,Object interceptor,InterceptorContainerDecorator container){
		Method methodToInvoke = stepInvoker.findMethod(step,interceptor);
		if(methodToInvoke==null) return new Object[]{};
		Class<?>[] parameterTypes = methodToInvoke.getParameterTypes();
		ArrayList parameters = new ArrayList();
		for (Class<?> parameterType : parameterTypes) {	
			parameters.add(container.instanceFor(parameterType));
		}
		return parameters.toArray();

	}


}
