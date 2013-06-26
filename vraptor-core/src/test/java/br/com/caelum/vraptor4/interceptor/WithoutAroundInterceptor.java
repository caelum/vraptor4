package br.com.caelum.vraptor4.interceptor;

import br.com.caelum.vraptor.Intercepts;
import br.com.caelum.vraptor4.AfterInvoke;
import br.com.caelum.vraptor4.BeforeInvoke;

@Intercepts
public class WithoutAroundInterceptor {

	@BeforeInvoke
	public void before() {
	}
	
	@AfterInvoke
	public void after() {
	}
}
