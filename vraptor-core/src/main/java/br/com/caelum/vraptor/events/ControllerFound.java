package br.com.caelum.vraptor.events;

import br.com.caelum.vraptor.controller.BeanClass;
import br.com.caelum.vraptor.controller.ControllerMethod;

public class ControllerFound {

	private final ControllerMethod method;

	public ControllerFound(ControllerMethod method) {
		this.method = method;
	}

	public ControllerMethod getMethod() {
		return method;
	}
	
	public BeanClass getController() {
		return method.getController();
	}

}
