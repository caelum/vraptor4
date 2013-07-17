package br.com.caelum.vraptor4.interceptor.example;

import br.com.caelum.vraptor.Intercepts;
import br.com.caelum.vraptor4.Accepts;
import br.com.caelum.vraptor4.interceptor.SimpleInterceptorStack;

@Intercepts
public class AcceptsInterceptorWithStackAsParameter {

	@Accepts
	public boolean accepts(SimpleInterceptorStack stack){
		return true;
	}
}
