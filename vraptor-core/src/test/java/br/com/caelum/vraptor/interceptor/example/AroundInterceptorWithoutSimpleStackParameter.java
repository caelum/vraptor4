package br.com.caelum.vraptor.interceptor.example;

import javax.enterprise.inject.Vetoed;

import br.com.caelum.vraptor.AroundCall;
import br.com.caelum.vraptor.Intercepts;

@Intercepts
@Vetoed
public class AroundInterceptorWithoutSimpleStackParameter {

	@AroundCall
	public void intercept(){
		
	}
}
