package br.com.caelum.vraptor4.interceptor;

import br.com.caelum.vraptor4.BeforeCall;
import br.com.caelum.vraptor4.Intercepts;

@Intercepts
public class InterceptorWithMoreThanOneBeforeCallMethod {

	@BeforeCall
	public void before1() {}

	@BeforeCall
	public void before2() {}

}
