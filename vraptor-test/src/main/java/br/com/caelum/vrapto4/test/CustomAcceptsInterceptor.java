package br.com.caelum.vrapto4.test;

import br.com.caelum.vraptor.AroundCall;
import br.com.caelum.vraptor.Intercepts;
import br.com.caelum.vraptor.interceptor.AcceptsWithAnnotations;
import br.com.caelum.vraptor.interceptor.SimpleInterceptorStack;

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
