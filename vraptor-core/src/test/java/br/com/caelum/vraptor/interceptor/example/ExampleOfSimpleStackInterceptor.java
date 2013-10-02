package br.com.caelum.vraptor.interceptor.example;

import javax.enterprise.inject.Vetoed;

import br.com.caelum.vraptor.AroundCall;
import br.com.caelum.vraptor.Intercepts;
import br.com.caelum.vraptor.interceptor.SimpleInterceptorStack;

@Intercepts
@Vetoed
public class ExampleOfSimpleStackInterceptor {

	@AroundCall
	public void around(SimpleInterceptorStack stack){
		stack.next();
	}
}
