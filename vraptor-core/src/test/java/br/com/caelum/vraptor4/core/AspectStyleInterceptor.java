package br.com.caelum.vraptor4.core;

import br.com.caelum.vraptor4.AfterCall;
import br.com.caelum.vraptor4.Intercepts;

@Intercepts
public class AspectStyleInterceptor{
	@AfterCall
	public void intercept() {}
}

