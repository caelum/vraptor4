package br.com.caelum.vraptor.interceptor.example;

import br.com.caelum.vraptor.AfterCall;
import br.com.caelum.vraptor.BeforeCall;
import br.com.caelum.vraptor.Intercepts;

@Intercepts
public class WithoutAroundInterceptor {

	@BeforeCall
	public void before() {
	}
	
	@AfterCall
	public void after() {
	}
}
