package br.com.caelum.vraptor4.interceptor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import net.vidageek.mirror.dsl.Mirror;
import net.vidageek.mirror.list.dsl.Matcher;
import net.vidageek.mirror.list.dsl.MirrorList;

public class StepInvoker {
	
	
	private class InvokeMatcher implements Matcher<Method> {

		private Class<? extends Annotation> step;

		public InvokeMatcher(Class<? extends Annotation> step) {
			this.step = step;
		}

		@Override
		public boolean accepts(Method element) {
			return element.isAnnotationPresent(this.step);
		}

	}
	

	public Object tryToInvoke(Object interceptor,Class<? extends Annotation> step,Object... params) {
		Mirror mirror = new Mirror();
		MirrorList<Method> possibleMethods = mirror.on(interceptor.getClass()).reflectAll().methods().matching(new InvokeMatcher(step));
		if(possibleMethods.isEmpty()) return null;
		if(possibleMethods.size() > 1){
			throw new IllegalStateException("You should not have more than one @"+step.getSimpleName()+" annotated method");
		}		
		Method beginMethod = possibleMethods.get(0);
		return new Mirror().on(interceptor).invoke().method(beginMethod).withArgs(params);
	}
		

}
