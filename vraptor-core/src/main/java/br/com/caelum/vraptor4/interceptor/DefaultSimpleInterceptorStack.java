package br.com.caelum.vraptor4.interceptor;

import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor4.controller.ControllerInstance;
import br.com.caelum.vraptor4.controller.ControllerMethod;

public class DefaultSimpleInterceptorStack implements SimpleInterceptorStack{
	
	private InterceptorStack delegate;
	private ControllerMethod controllerMethod;
	private ControllerInstance controllerInstance;

	public DefaultSimpleInterceptorStack(InterceptorStack delegate,ControllerMethod controllerMethod,ControllerInstance controllerInstance) {
		super();
		this.delegate = delegate;
		this.controllerMethod = controllerMethod;
		this.controllerInstance = controllerInstance;
	}

	@Override
	public void next(){		
		delegate.next(controllerMethod,controllerInstance.getController());
	}

	

}
