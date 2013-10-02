package br.com.caelum.vraptor.interceptor.example;

import javax.enterprise.inject.Vetoed;

import br.com.caelum.vraptor.AfterCall;
import br.com.caelum.vraptor.BeforeCall;
import br.com.caelum.vraptor.Intercepts;

@Intercepts
@Vetoed
public class WithoutAroundInterceptor {

	@BeforeCall
	public void before() {
	}
	
	@AfterCall
	public void after() {
	}
}
