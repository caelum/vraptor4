package br.com.caelum.vraptor.interceptor.example;

import br.com.caelum.vraptor.AroundCall;
import br.com.caelum.vraptor.Intercepts;

@Intercepts
public class AroundInterceptorWithoutSimpleStackParameter {

	@AroundCall
	public void intercept(){
		
	}
}
