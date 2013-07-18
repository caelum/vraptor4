package br.com.caelum.vrapto4.test;

import br.com.caelum.vraptor4.AroundCall;
import br.com.caelum.vraptor4.Intercepts;
import br.com.caelum.vraptor4.interceptor.AcceptsWithAnnotations;
import br.com.caelum.vraptor4.interceptor.SimpleInterceptorStack;

@Intercepts
@AcceptsWithAnnotations(NotLogged.class)
public class CustomAcceptsInterceptor {

	@AroundCall
	public void around(SimpleInterceptorStack stack){
		System.out.println("notlogged antes");
		stack.next();
		System.out.println("notlogged depois");
	}
}
