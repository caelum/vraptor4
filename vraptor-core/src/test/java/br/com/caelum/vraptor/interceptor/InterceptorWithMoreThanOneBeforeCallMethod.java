package br.com.caelum.vraptor.interceptor;

import javax.enterprise.inject.Vetoed;

import br.com.caelum.vraptor.BeforeCall;
import br.com.caelum.vraptor.Intercepts;

@Intercepts
@Vetoed
public class InterceptorWithMoreThanOneBeforeCallMethod {

	@BeforeCall
	public void before1() {}

	@BeforeCall
	public void before2() {}

}
