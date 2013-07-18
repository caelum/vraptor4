package br.com.caelum.vraptor4.interceptor.example;

import br.com.caelum.vraptor4.BeforeCall;
import br.com.caelum.vraptor4.Intercepts;
import br.com.caelum.vraptor4.core.InterceptorStack;

@Intercepts
public class BeforeAfterInterceptorWithStackAsParameter{
	
	@BeforeCall
	public void before(InterceptorStack interceptorStack) {
		
	}

}
