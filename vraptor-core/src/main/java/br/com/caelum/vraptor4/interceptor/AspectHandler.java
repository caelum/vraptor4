package br.com.caelum.vraptor4.interceptor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import javax.interceptor.AroundInvoke;

import br.com.caelum.vraptor4.AfterInvoke;
import br.com.caelum.vraptor4.BeforeInvoke;
import net.vidageek.mirror.dsl.Mirror;
import net.vidageek.mirror.list.dsl.Matcher;
import net.vidageek.mirror.list.dsl.MirrorList;

public class AspectHandler {

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

	private Object interceptor;

	public AspectHandler(Object interceptor) {
		this.interceptor = interceptor;

	}

	public void hanle() {
		Mirror mirror = new Mirror();
		
		MirrorList<Method> beginPossibleMethods = getAnnotatedMethod(mirror,new InvokeMatcher(BeforeInvoke.class));
		
		if(beginPossibleMethods.isEmpty()) return;
		if(beginPossibleMethods.size() > 1){
			throw new IllegalStateException("You should not have more than one @Begin annotated method");
		}		
		Method beginMethod = beginPossibleMethods.get(0);
		mirror.on(interceptor).invoke().method(beginMethod).withoutArgs();		
		
		MirrorList<Method> aroundInvokePossibleMethods = getAnnotatedMethod(mirror,new InvokeMatcher(AroundInvoke.class));
		
		if(aroundInvokePossibleMethods.isEmpty()) return;
		if(aroundInvokePossibleMethods.size() > 1){
			throw new IllegalStateException("You should not have more than one @AroundInvoke annotated method");
		}	
		
		Method aroundInvokeMethod = aroundInvokePossibleMethods.get(0);
		mirror.on(interceptor).invoke().method(aroundInvokeMethod).withoutArgs();
		
		MirrorList<Method> afterPossibleMethods = getAnnotatedMethod(mirror,new InvokeMatcher(AfterInvoke.class));
		
		if(afterPossibleMethods.isEmpty()) return;
		if(afterPossibleMethods.size() > 1){
			throw new IllegalStateException("You should not have more than one @After annotated method");
		}		
		Method afterMethod = afterPossibleMethods.get(0);
		mirror.on(interceptor).invoke().method(afterMethod).withoutArgs();		
		
	}

	private MirrorList<Method> getAnnotatedMethod(Mirror mirror,Matcher<Method> matcher) {
		MirrorList<Method> methods = mirror.on(interceptor.getClass()).reflectAll().methods()
				.matching(matcher);
		return methods;
	}

}
