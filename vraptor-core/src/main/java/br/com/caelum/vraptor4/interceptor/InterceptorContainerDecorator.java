package br.com.caelum.vraptor4.interceptor;

import java.util.HashMap;

import br.com.caelum.vraptor4.ioc.Container;

class InterceptorContainerDecorator implements Container {

	private Container delegate;
	private HashMap<ParameterClass, Object> onDemandObjects = new HashMap<ParameterClass,Object>();

	public InterceptorContainerDecorator(Container container,Object... onDemandObjects) {
		this.delegate = container;
		for (Object object : onDemandObjects) {
			this.onDemandObjects.put(new ParameterClass(object.getClass()),object);
		}
	}
	
	@SuppressWarnings("unchecked")
	public <T> T instanceFor(Class<T> type) {
		ParameterClass parameterClass = new ParameterClass(type);
		T onDemandObject = (T) onDemandObjects.get(parameterClass);
		return onDemandObject!=null?onDemandObject:delegate.instanceFor(type);
	}

	public <T> boolean canProvide(Class<T> type) {	
		return onDemandObjects.containsKey(new ParameterClass(type)) || delegate.canProvide(type);
	}


}
