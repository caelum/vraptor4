package br.com.caelum.vraptor.interceptor;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import br.com.caelum.vraptor.controller.ControllerInstance;
import br.com.caelum.vraptor.controller.ControllerMethod;
import br.com.caelum.vraptor.core.InterceptorStack;

@RequestScoped
public class DefaultSimpleInterceptorStack implements SimpleInterceptorStack {

	private final InterceptorStack delegate;
	private final ControllerMethod controllerMethod;
	private final ControllerInstance controllerInstance;

	/** 
	 * @deprecated CDI eyes only
	 */
	protected DefaultSimpleInterceptorStack() {
		this(null, null, null);
	}

	@Inject
	public DefaultSimpleInterceptorStack(InterceptorStack delegate, ControllerMethod controllerMethod,
			ControllerInstance controllerInstance) {
		this.delegate = delegate;
		this.controllerMethod = controllerMethod;
		this.controllerInstance = controllerInstance;
	}
	
	@Override
	public void next() {
		delegate.next(controllerMethod, controllerInstance.getController());
	}

}
