package br.com.caelum.vraptor4.interceptor.example;

import br.com.caelum.vraptor4x.AfterCall;
import br.com.caelum.vraptor4x.AroundCall;
import br.com.caelum.vraptor4x.BeforeCall;
import br.com.caelum.vraptor4x.interceptor.SimpleInterceptorStack;

public class BaseInterceptor {

	@BeforeCall
	public void begin(){
		
	}

	@AroundCall
	public void intercept(SimpleInterceptorStack sis){
	}
		

	@AfterCall
	public void after() {
		
	}
}
