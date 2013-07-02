package br.com.caelum.vraptor4.controller;

public class DefaultControllerInstance implements ControllerInstance {

	private Object controller;

	public DefaultControllerInstance(Object instance) {
		super();
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
