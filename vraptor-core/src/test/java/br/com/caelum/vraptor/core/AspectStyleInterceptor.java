package br.com.caelum.vraptor.core;

import br.com.caelum.vraptor.AfterCall;
import br.com.caelum.vraptor.Intercepts;

@Intercepts
public class AspectStyleInterceptor{
	@AfterCall
	public void intercept() {}
}

