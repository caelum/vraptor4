package br.com.caelum.vraptor.controller;

import javax.enterprise.inject.Vetoed;

@Vetoed
public class DefaultControllerInstance implements ControllerInstance {

	private Object controller;

	public DefaultControllerInstance(Object instance) {
		this.controller = instance;
	}
	
	@Override
	public Object getController() {
		return controller;
	}
	
	@Override
	public BeanClass getBeanClass(){
		return new DefaultBeanClass(controller.getClass());
	}
	
}
