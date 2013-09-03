package br.com.caelum.vraptor.interceptor.example;

import br.com.caelum.vraptor.Accepts;
import br.com.caelum.vraptor.Intercepts;
import br.com.caelum.vraptor.interceptor.SimpleInterceptorStack;

@Intercepts
public class AcceptsInterceptorWithStackAsParameter {

	@Accepts
	public boolean accepts(SimpleInterceptorStack stack){
		return true;
	}
}
