package br.com.caelum.vraptor.interceptor.example;

import javax.enterprise.inject.Vetoed;

import br.com.caelum.vraptor.AfterCall;
import br.com.caelum.vraptor.AroundCall;
import br.com.caelum.vraptor.BeforeCall;
import br.com.caelum.vraptor.Intercepts;
import br.com.caelum.vraptor.interceptor.SimpleInterceptorStack;

@Intercepts
@Vetoed
public class InterceptorWithInheritance extends BaseInterceptor{
	
	@Override
	@BeforeCall
	public void begin(){
		
	}

	@Override
	@AroundCall
	public void intercept(SimpleInterceptorStack sis){
	}
		

	@Override
	@AfterCall
	public void after() {
		
	}
}
