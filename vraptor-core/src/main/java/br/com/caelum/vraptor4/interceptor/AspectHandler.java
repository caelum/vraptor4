package br.com.caelum.vraptor4.interceptor;

import javax.interceptor.AroundInvoke;

import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor4.Accepts;
import br.com.caelum.vraptor4.AfterInvoke;
import br.com.caelum.vraptor4.BeforeInvoke;
import br.com.caelum.vraptor4.controller.ControllerMethod;

public class AspectHandler {

	private StepInvoker stepInvoker;

	private Object interceptor;

	public AspectHandler(Object interceptor, StepInvoker stepInvoker) {
		this.interceptor = interceptor;
		this.stepInvoker = stepInvoker;

	}

	public void handle(InterceptorStack stack,ControllerMethod controllerMethod,Object controllerInstance) {
		Object returnObject = stepInvoker.tryToInvoke(interceptor,Accepts.class);
		
		boolean accepts = true;
		
		if(returnObject!=null){
			if(!returnObject.getClass().equals(Boolean.class)){
				throw new IllegalStateException("@Accepts method should return boolean");
			}
			accepts = (Boolean) returnObject;
		}			
		
		if(accepts){			
			stepInvoker.tryToInvoke(interceptor,BeforeInvoke.class);						
			stepInvoker.tryToInvoke(interceptor,AroundInvoke.class,stack,controllerMethod,controllerInstance);	
			stepInvoker.tryToInvoke(interceptor,AfterInvoke.class);
		}

	}


}
