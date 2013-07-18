package br.com.caelum.vraptor4.interceptor.example;

import br.com.caelum.vraptor4.AfterCall;
import br.com.caelum.vraptor4.AroundCall;
import br.com.caelum.vraptor4.BeforeCall;
import br.com.caelum.vraptor4.Intercepts;
import br.com.caelum.vraptor4.interceptor.SimpleInterceptorStack;

@Intercepts
public class InterceptorWithInheritance extends BaseInterceptor{
	
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
