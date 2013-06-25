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

	public void handle() {
		Object returnObject = stepInvoker.tryToInvoke(interceptor,Accepts.class);
		
		boolean accepts = true;
		
		if(returnObject!=null){
			if(!returnObject.getClass().equals(Boolean.class)){
				throw new IllegalStateException("@Accepts method should return boolean");
			}
			accepts = (Boolean) returnObject;
		}			
		
		if(accepts){			
			stepInvoker.tryToInvoke(interceptor,BeforeInvoke.class);						
			stepInvoker.tryToInvoke(interceptor,AroundInvoke.class);	
			stepInvoker.tryToInvoke(interceptor,AfterInvoke.class);
		}

	}


}
