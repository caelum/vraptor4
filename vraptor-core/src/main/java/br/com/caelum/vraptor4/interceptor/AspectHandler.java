package br.com.caelum.vraptor4.interceptor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;

import javax.interceptor.AroundInvoke;

import br.com.caelum.vraptor.core.InterceptorHandler;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.ioc.Container;
import br.com.caelum.vraptor4.Accepts;
import br.com.caelum.vraptor4.AfterInvoke;
import br.com.caelum.vraptor4.BeforeInvoke;
import br.com.caelum.vraptor4.controller.ControllerMethod;
import br.com.caelum.vraptor4.controller.DefaultControllerInstance;

public class AspectHandler implements InterceptorHandler{

	private StepInvoker stepInvoker;
	private Container container;
	private Class<?> interceptorClass;

	public AspectHandler(Class<?> interceptorClass, StepInvoker stepInvoker,Container container) {
		this.interceptorClass = interceptorClass;
		this.stepInvoker = stepInvoker;
		this.container = container;

	}
		
	public void execute(InterceptorStack stack,ControllerMethod controllerMethod,Object currentController) {
		Object interceptor = container.instanceFor(interceptorClass);
		DefaultControllerInstance controllerInstance = new DefaultControllerInstance(currentController);
		InterceptorStackDecorator interceptorStackDecorator = new InterceptorStackDecorator(stack);
		InterceptorContainerDecorator interceptorContainer = new InterceptorContainerDecorator(container,interceptorStackDecorator,controllerMethod,controllerInstance,new DefaultSimplerInterceptorStack(interceptorStackDecorator, controllerMethod, controllerInstance));
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
			if(noAround(interceptor) && !interceptorStackDecorator.isNexted()){
				stack.next(controllerMethod,controllerInstance.getController());
			}
			else{
			   stepInvoker.tryToInvoke(interceptor,AroundInvoke.class,parametersFor(AroundInvoke.class,interceptor,interceptorContainer));
			}
			stepInvoker.tryToInvoke(interceptor,AfterInvoke.class);
		} else {
			stack.next(controllerMethod, controllerInstance.getController());
		}

	}
	
	private boolean noAround(Object interceptor) {
		return stepInvoker.findMethod(AroundInvoke.class, interceptor) == null;
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
