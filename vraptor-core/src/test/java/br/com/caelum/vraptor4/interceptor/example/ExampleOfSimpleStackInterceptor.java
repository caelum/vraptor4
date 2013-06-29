package br.com.caelum.vraptor4.interceptor.example;

import br.com.caelum.vraptor.Intercepts;
import br.com.caelum.vraptor4.AroundCall;
import br.com.caelum.vraptor4.interceptor.SimpleInterceptorStack;

@Intercepts
public class ExampleOfSimpleStackInterceptor {

	@AroundCall
	public void around(SimpleInterceptorStack stack){
		stack.next();
	}
}
