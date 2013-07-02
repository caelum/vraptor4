package br.com.caelum.vraptor4.interceptor.example;

import br.com.caelum.vraptor.Intercepts;
import br.com.caelum.vraptor4.AroundCall;

@Intercepts
public class AroundInterceptorWithoutSimpleStackParameter {

	@AroundCall
	public void intercept(){
		
	}
}
