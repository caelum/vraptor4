package br.com.caelum.vraptor4.interceptor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;

import net.vidageek.mirror.dsl.Mirror;
import net.vidageek.mirror.exception.MirrorException;
import net.vidageek.mirror.list.dsl.Matcher;
import net.vidageek.mirror.list.dsl.MirrorList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor4.InterceptionException;

public class StepInvoker {
	
	private class InvokeMatcher implements Matcher<Method> {

		private Class<? extends Annotation> step;

		public InvokeMatcher(Class<? extends Annotation> step) {
			this.step = step;
		}

		@Override
		public boolean accepts(Method element) {
			if(element.getDeclaringClass().getName().contains("$")){
				return false;
			}
			return element.isAnnotationPresent(this.step);
		}

	}
	

	public Object tryToInvoke(Object interceptor,Class<? extends Annotation> step,Object... params) {
		Method stepMethod = findMethod(step, interceptor.getClass());
		if (stepMethod==null){
			return null;
		}
		Object returnObject = invokeMethod(interceptor, stepMethod, params);
		if(stepMethod.getReturnType().equals(void.class)){
			return new VoidReturn();
		}
		return returnObject;				
	}


	private Object invokeMethod(Object interceptor, Method stepMethod,
			Object... params) {
		try {
			Object returnObject = createMirror().on(interceptor).invoke().method(stepMethod).withArgs(params);
			return returnObject;
		} catch (MirrorException e) {
			throw new InterceptionException(e.getCause());
		}
	}


	public Method findMethod(Class<? extends Annotation> step,Class<?> interceptorClass) {
		MirrorList<Method> possibleMethods = createMirror().on(interceptorClass).reflectAll().methods().matching(new InvokeMatcher(step));
		if (possibleMethods.size() > 1) {
			throw new IllegalStateException("You should not have more than one @"+step.getSimpleName()+" annotated method");
		}		
		if(possibleMethods.isEmpty()){
			return null;
		}
		Method stepMethod = possibleMethods.get(0);
		return stepMethod;
	}


	private Mirror createMirror() {
		return new Mirror();
	}
		
}
