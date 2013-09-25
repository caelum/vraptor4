package br.com.caelum.vraptor.interceptor.example;

import javax.enterprise.inject.Vetoed;

import br.com.caelum.vraptor.Accepts;
import br.com.caelum.vraptor.Intercepts;
import br.com.caelum.vraptor.interceptor.SimpleInterceptorStack;

@Intercepts
@Vetoed
public class AcceptsInterceptorWithStackAsParameter {

	@Accepts
	public boolean accepts(SimpleInterceptorStack stack){
		return true;
	}
}
