package br.com.caelum.vraptor.interceptor.example;

import javax.enterprise.inject.Vetoed;

import br.com.caelum.vraptor.BeforeCall;
import br.com.caelum.vraptor.Intercepts;
import br.com.caelum.vraptor.core.InterceptorStack;

@Intercepts
@Vetoed
public class BeforeAfterInterceptorWithStackAsParameter{
	
	@BeforeCall
	public void before(InterceptorStack interceptorStack) {
		
	}

}
