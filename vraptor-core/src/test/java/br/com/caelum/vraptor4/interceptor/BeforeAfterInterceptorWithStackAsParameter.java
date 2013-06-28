package br.com.caelum.vraptor4.interceptor;

import br.com.caelum.vraptor.Intercepts;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor4.BeforeCall;

@Intercepts
public class BeforeAfterInterceptorWithStackAsParameter{
	
	@BeforeCall
	public void before(InterceptorStack interceptorStack) {
		
	}

}
