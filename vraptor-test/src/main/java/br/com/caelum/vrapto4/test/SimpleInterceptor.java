package br.com.caelum.vrapto4.test;

import br.com.caelum.vraptor.AfterCall;
import br.com.caelum.vraptor.AroundCall;
import br.com.caelum.vraptor.BeforeCall;
import br.com.caelum.vraptor.Intercepts;
import br.com.caelum.vraptor.interceptor.SimpleInterceptorStack;

@Intercepts
public class SimpleInterceptor {

	@BeforeCall
	public void before() {
		System.out.println("mente para mim");
	}

	@AroundCall
	public void around(SimpleInterceptorStack stack){
		System.out.println("cantando antes");
		stack.next();
		System.out.println("cantando depois");
	}

	@AfterCall
	public void after() {
		System.out.println("eh assim que eu gosto");
	}
}
