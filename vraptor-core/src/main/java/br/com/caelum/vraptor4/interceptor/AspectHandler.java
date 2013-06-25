package br.com.caelum.vraptor4.interceptor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import javax.interceptor.AroundInvoke;

import net.vidageek.mirror.dsl.Mirror;
import net.vidageek.mirror.list.dsl.Matcher;
import net.vidageek.mirror.list.dsl.MirrorList;
import br.com.caelum.vraptor4.Accepts;
import br.com.caelum.vraptor4.AfterInvoke;
import br.com.caelum.vraptor4.BeforeInvoke;

public class AspectHandler {

	private StepInvoker stepInvoker;

	private Object interceptor;

	public AspectHandler(Object interceptor, StepInvoker stepInvoker) {
		this.interceptor = interceptor;
		this.stepInvoker = stepInvoker;

	}

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

	public void handle() {
		Mirror mirror = new Mirror();
		
		MirrorList<Method> acceptsPossibleMethods = getAnnotatedMethod(mirror,
				new InvokeMatcher(Accepts.class));
		
		Object returnObject = stepInvoker.tryToInvoke(interceptor, acceptsPossibleMethods,Accepts.class);
		
		boolean accepts = true;
		
		if(returnObject!=null){
			if(!returnObject.getClass().equals(Boolean.class)){
				throw new IllegalStateException("@Accepts method should return boolean");
			}
			accepts = (Boolean) returnObject;
		}			
		
		if(accepts){
			
			MirrorList<Method> beginPossibleMethods = getAnnotatedMethod(mirror,
					new InvokeMatcher(BeforeInvoke.class));

			stepInvoker.tryToInvoke(interceptor,beginPossibleMethods, BeforeInvoke.class);			
			
			MirrorList<Method> aroundInvokePossibleMethods = getAnnotatedMethod(
					mirror, new InvokeMatcher(AroundInvoke.class));
			stepInvoker
					.tryToInvoke(interceptor,aroundInvokePossibleMethods, AroundInvoke.class);
	
			MirrorList<Method> afterPossibleMethods = getAnnotatedMethod(mirror,
					new InvokeMatcher(AfterInvoke.class));
			stepInvoker.tryToInvoke(interceptor,afterPossibleMethods, AfterInvoke.class);
		}

	}

	private MirrorList<Method> getAnnotatedMethod(Mirror mirror,
			Matcher<Method> matcher) {
		MirrorList<Method> methods = mirror.on(interceptor.getClass())
				.reflectAll().methods().matching(matcher);
		return methods;
	}

}
