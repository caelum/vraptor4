package br.com.caelum.vraptor4.interceptor.example;

import br.com.caelum.vraptor4.AfterCall;
import br.com.caelum.vraptor4.BeforeCall;
import br.com.caelum.vraptor4.Intercepts;

@Intercepts
public class WithoutAroundInterceptor {

	@BeforeCall
	public void before() {
	}
	
	@AfterCall
	public void after() {
	}
}
