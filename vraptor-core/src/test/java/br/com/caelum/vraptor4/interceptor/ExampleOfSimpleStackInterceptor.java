package br.com.caelum.vraptor4.interceptor;

import br.com.caelum.vraptor.Intercepts;
import br.com.caelum.vraptor4.AroundCall;

@Intercepts
public class ExampleOfSimpleStackInterceptor {

	@AroundCall
	public void around(SimpleInterceptorStack stack){
		stack.next();
	}
}
