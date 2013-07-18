package br.com.caelum.vraptor4.interceptor.example;

import br.com.caelum.vraptor4.Intercepts;
import br.com.caelum.vraptor4.core.InterceptorStack;
import br.com.caelum.vraptor4x.BeforeCall;

@Intercepts
public class BeforeAfterInterceptorWithStackAsParameter{
	
	@BeforeCall
	public void before(InterceptorStack interceptorStack) {
		
	}

}
