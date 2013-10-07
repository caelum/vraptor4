package br.com.caelum.vraptor.core;

import javax.enterprise.inject.Vetoed;

import br.com.caelum.vraptor.AfterCall;
import br.com.caelum.vraptor.Intercepts;

@Intercepts
@Vetoed
public class AspectStyleInterceptor{
	@AfterCall
	public void intercept() {}
}

