package br.com.caelum.vraptor4.interceptor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import javax.interceptor.AroundInvoke;

import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.ioc.Container;
import br.com.caelum.vraptor4.Accepts;
import br.com.caelum.vraptor4.AfterInvoke;
import br.com.caelum.vraptor4.BeforeInvoke;
import br.com.caelum.vraptor4.controller.ControllerInstance;
import br.com.caelum.vraptor4.controller.ControllerMethod;

public class AspectHandler {

	private StepInvoker stepInvoker;
	private Object interceptor;
	private Container container;

	public AspectHandler(Object interceptor, StepInvoker stepInvoker,Container container) {
		this.interceptor = interceptor;
		this.stepInvoker = stepInvoker;
		this.container = container;

	}
		
	public void handle(InterceptorStack stack,ControllerMethod controllerMethod,ControllerInstance controllerInstance) {
		InterceptorContainerDecorator interceptorContainer = new InterceptorContainerDecorator(container,stack,controllerMethod,controllerInstance,new DefaultSimplerInterceptorStack(stack, controllerMethod, controllerInstance));
		Object returnObject = stepInvoker.tryToInvoke(interceptor,Accepts.class,parametersFor(Accepts.class,interceptor,interceptorContainer));
		
		boolean accepts = true;
		if(returnObject!=null){			
			if(!returnObject.getClass().equals(Boolean.class)){
				throw new IllegalStateException("@Accepts method should return boolean");
			}
			accepts = (Boolean) returnObject;
		}			
		
		if(accepts){			
			stepInvoker.tryToInvoke(interceptor,BeforeInvoke.class);
			stepInvoker.tryToInvoke(interceptor,AroundInvoke.class,parametersFor(AroundInvoke.class,interceptor,interceptorContainer));
//			if(noAround() && stack.notNexteada()){
//				stack.next(controllerMethod,controllerInstance.getController());
//			}
			stepInvoker.tryToInvoke(interceptor,AfterInvoke.class);
		} else {
			stack.next(controllerMethod, controllerInstance.getController());
		}

	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Object[] parametersFor(Class<? extends Annotation> step,Object interceptor,InterceptorContainerDecorator container){
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
