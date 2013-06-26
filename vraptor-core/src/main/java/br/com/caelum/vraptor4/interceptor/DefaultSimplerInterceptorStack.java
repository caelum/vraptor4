package br.com.caelum.vraptor4.interceptor;

import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor4.controller.ControllerMethod;

public class DefaultSimplerInterceptorStack implements SimpleInterceptorStack{
	
	private InterceptorStack delegate;
	private ControllerMethod controllerMethod;
	private Object controllerInstance;

	public DefaultSimplerInterceptorStack(InterceptorStack delegate,ControllerMethod controllerMethod,Object controllerInstance) {
		super();
		this.delegate = delegate;
		this.controllerMethod = controllerMethod;
		this.controllerInstance = controllerInstance;
	}

	@Override
	public void next(){		
		delegate.next(controllerMethod,controllerInstance);
	}

	

}
