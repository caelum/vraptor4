package br.com.caelum.vraptor4.interceptor;

import javax.inject.Inject;

import br.com.caelum.vraptor4.controller.ControllerInstance;
import br.com.caelum.vraptor4.controller.ControllerMethod;
import br.com.caelum.vraptor4.core.InterceptorStack;
import br.com.caelum.vraptor4.ioc.RequestScoped;

@RequestScoped
public class DefaultSimpleInterceptorStack implements SimpleInterceptorStack {

	private InterceptorStack delegate;
	private ControllerMethod controllerMethod;
	private ControllerInstance controllerInstance;

	@Inject
	public DefaultSimpleInterceptorStack(InterceptorStack delegate,
			ControllerMethod controllerMethod,
			ControllerInstance controllerInstance) {
		super();
		this.delegate = delegate;
		this.controllerMethod = controllerMethod;
		this.controllerInstance = controllerInstance;
	}
	
	@Deprecated
	public DefaultSimpleInterceptorStack() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void next() {
		delegate.next(controllerMethod, controllerInstance.getController());
	}

}
