package br.com.caelum.vraptor4.interceptor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import net.vidageek.mirror.dsl.Mirror;
import net.vidageek.mirror.exception.MirrorException;
import net.vidageek.mirror.list.dsl.Matcher;
import net.vidageek.mirror.list.dsl.MirrorList;
import br.com.caelum.vraptor4.InterceptionException;

import com.google.common.base.Throwables;

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

	public Object tryToInvoke(Object interceptor, Method stepMethod, Object... params) {
		if (stepMethod==null){
			return null;
		}
		Object returnObject = invokeMethod(interceptor, stepMethod, params);
		if(stepMethod.getReturnType().equals(void.class)){
			return new VoidReturn();
		}
		return returnObject;
	}

	private Object invokeMethod(Object interceptor, Method stepMethod, Object... params) {
		try {
			Object returnObject = new Mirror().on(interceptor).invoke().method(stepMethod).withArgs(params);
			return returnObject;
		} catch (MirrorException e) {
			// we dont wanna wrap it if it is a simple controller business logic exception
			Throwables.propagateIfInstanceOf(e.getCause(), ApplicationLogicException.class);
			throw new InterceptionException(e.getCause());
		}
	}

	public Method findMethod(Class<? extends Annotation> step,Class<?> interceptorClass) {
		MirrorList<Method> possibleMethods = findPossibleMethods(step, interceptorClass);
		if (possibleMethods.size() > 1 && isNotSameClass(possibleMethods, interceptorClass)) {
			throw new IllegalStateException("You should not " +
				"have more than one @"+step.getSimpleName()+" annotated method");
		}
		return possibleMethods.isEmpty() ? null : possibleMethods.get(0);
	}

	private MirrorList<Method> findPossibleMethods(Class<? extends Annotation> step, Class<?> classs) {
		return new Mirror().on(classs).reflectAll().methods().matching(new InvokeMatcher(step));
	}

	private boolean isNotSameClass(MirrorList<Method> methods, Class<?> interceptorClass) {

		for (Method possibleMethod : methods) {
			if(!possibleMethod.getDeclaringClass().equals(interceptorClass)) {
				return false;
			}
		}
		return true;
	}

}