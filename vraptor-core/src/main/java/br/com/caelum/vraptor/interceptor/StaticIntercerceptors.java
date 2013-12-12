package br.com.caelum.vraptor.interceptor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.enterprise.inject.Vetoed;

import br.com.caelum.vraptor.controller.ControllerMethod;

@Vetoed
public class StaticIntercerceptors {

	private Map<ControllerMethod,Object> methods = new ConcurrentHashMap<>();
	private final Object value = new Object();

	public void add(ControllerMethod controllerMethod) {
		methods.put(controllerMethod, value);
	}
	
	public boolean contains(ControllerMethod method) {
		return methods.containsKey(method);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public boolean accepts(Class interceptorClass) {
		return interceptorClass.getAnnotation(StaticAccepts.class)!=null;
	}
	
	
}
