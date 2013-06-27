package br.com.caelum.vraptor4.interceptor;

import br.com.caelum.vraptor.Intercepts;
import br.com.caelum.vraptor4.AfterCall;
import br.com.caelum.vraptor4.BeforeCall;

@Intercepts
public class WithoutAroundInterceptor {

	@BeforeCall
	public void before() {
	}
	
	@AfterCall
	public void after() {
	}
}
