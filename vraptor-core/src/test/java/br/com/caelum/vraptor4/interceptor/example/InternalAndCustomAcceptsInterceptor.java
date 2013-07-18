package br.com.caelum.vraptor4.interceptor.example;

import br.com.caelum.vraptor4.Accepts;
import br.com.caelum.vraptor4x.interceptor.AcceptsWithAnnotations;

@AcceptsWithAnnotations(NotLogged.class)
public class InternalAndCustomAcceptsInterceptor {
	
	@Accepts
	public boolean accepts(){
		return true;
	}

}
