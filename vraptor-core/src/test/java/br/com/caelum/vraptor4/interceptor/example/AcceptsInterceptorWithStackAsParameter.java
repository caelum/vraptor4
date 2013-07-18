package br.com.caelum.vraptor4.interceptor.example;

import br.com.caelum.vraptor4.Intercepts;
import br.com.caelum.vraptor4x.Accepts;
import br.com.caelum.vraptor4x.interceptor.SimpleInterceptorStack;

@Intercepts
public class AcceptsInterceptorWithStackAsParameter {

	@Accepts
	public boolean accepts(SimpleInterceptorStack stack){
		return true;
	}
}
