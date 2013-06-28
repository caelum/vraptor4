package br.com.caelum.vraptor4.interceptor;

import br.com.caelum.vraptor.Intercepts;
import br.com.caelum.vraptor4.Accepts;

@Intercepts
public class AcceptsInterceptorWithStackAsParameter {

	@Accepts
	public boolean accepts(SimpleInterceptorStack stack){
		return true;
	}
}
