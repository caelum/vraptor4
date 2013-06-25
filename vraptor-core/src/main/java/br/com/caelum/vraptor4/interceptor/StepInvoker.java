package br.com.caelum.vraptor4.interceptor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;

import net.vidageek.mirror.dsl.Mirror;

public class StepInvoker {

	public Object tryToInvoke(Object interceptor,List<Method> possibleMethods,
			Class<? extends Annotation> step) {
		if(possibleMethods.isEmpty()) return null;
		if(possibleMethods.size() > 1){
			throw new IllegalStateException("You should not have more than one @"+step.getSimpleName()+" annotated method");
		}		
		Method beginMethod = possibleMethods.get(0);
		return new Mirror().on(interceptor).invoke().method(beginMethod).withoutArgs();
	}

}
