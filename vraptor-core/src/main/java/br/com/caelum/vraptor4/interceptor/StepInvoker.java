package br.com.caelum.vraptor4.interceptor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import net.vidageek.mirror.dsl.Mirror;
import net.vidageek.mirror.list.dsl.Matcher;
import net.vidageek.mirror.list.dsl.MirrorList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StepInvoker {
	
	private static final Logger logger = LoggerFactory
			.getLogger(StepInvoker.class);
	
	private class InvokeMatcher implements Matcher<Method> {

		private Class<? extends Annotation> step;

		public InvokeMatcher(Class<? extends Annotation> step) {
			this.step = step;
		}

		@Override
		public boolean accepts(Method element) {
			if(element.getDeclaringClass().getName().contains("$")){
				logger.debug("Ignoring possible proxy {}",element.getDeclaringClass());
				return false;
			}
			return element.isAnnotationPresent(this.step);
		}

	}
	

	public Object tryToInvoke(Object interceptor,Class<? extends Annotation> step,SignatureAcceptor acceptor,Object... params) {
		Method stepMethod = findMethod(step, interceptor);
		if(stepMethod==null){
			return null;
		}

		
		if(!acceptor.accepts(stepMethod)){			
			throw new IllegalArgumentException(acceptor.errorMessage());
		}
		Object returnObject = createMirror().on(interceptor).invoke().method(stepMethod).withArgs(params);
		if(stepMethod.getReturnType().equals(void.class)){
			return new VoidReturn();
		}
		return returnObject;				
	}


	public Method findMethod(Class<? extends Annotation> step,Object interceptor) {		
		MirrorList<Method> possibleMethods = createMirror().on(interceptor.getClass()).reflectAll().methods().matching(new InvokeMatcher(step));
		if(possibleMethods.size() > 1){
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
