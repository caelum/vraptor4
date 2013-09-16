package br.com.caelum.vraptor.interceptor;

import br.com.caelum.vraptor.BeforeCall;
import br.com.caelum.vraptor.Intercepts;

@Intercepts
public class InterceptorWithMoreThanOneBeforeCallMethod {

	@BeforeCall
	public void before1() {}

	@BeforeCall
	public void before2() {}

}
