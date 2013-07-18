package br.com.caelum.vraptor4.interceptor.example;

import br.com.caelum.vraptor4.Intercepts;
import br.com.caelum.vraptor4x.AroundCall;

@Intercepts
public class AroundInterceptorWithoutSimpleStackParameter {

	@AroundCall
	public void intercept(){
		
	}
}
