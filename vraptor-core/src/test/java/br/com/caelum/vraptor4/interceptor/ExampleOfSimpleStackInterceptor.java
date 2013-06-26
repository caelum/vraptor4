package br.com.caelum.vraptor4.interceptor;

import javax.interceptor.AroundInvoke;

import br.com.caelum.vraptor.Intercepts;

@Intercepts
public class ExampleOfSimpleStackInterceptor {

	@AroundInvoke
	public void around(SimpleInterceptorStack stack){
		stack.next();
	}
}
