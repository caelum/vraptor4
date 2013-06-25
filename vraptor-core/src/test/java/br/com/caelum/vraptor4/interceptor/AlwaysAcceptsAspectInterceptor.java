package br.com.caelum.vraptor4.interceptor;

import javax.interceptor.AroundInvoke;

import br.com.caelum.vraptor.Intercepts;
import br.com.caelum.vraptor4.AfterInvoke;
import br.com.caelum.vraptor4.BeforeInvoke;

@Intercepts
public class AlwaysAcceptsAspectInterceptor{
	
	@BeforeInvoke
	public void begin(){
		
	}

	@AroundInvoke
	public void intercept(){
		
	}

	@AfterInvoke
	public void after() {
		
	}
}
