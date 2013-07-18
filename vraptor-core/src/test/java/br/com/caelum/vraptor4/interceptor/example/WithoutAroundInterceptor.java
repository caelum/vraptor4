package br.com.caelum.vraptor4.interceptor.example;

import br.com.caelum.vraptor4.Intercepts;
import br.com.caelum.vraptor4x.AfterCall;
import br.com.caelum.vraptor4x.BeforeCall;

@Intercepts
public class WithoutAroundInterceptor {

	@BeforeCall
	public void before() {
	}
	
	@AfterCall
	public void after() {
	}
}
