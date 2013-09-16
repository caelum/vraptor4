package br.com.caelum.vraptor.interceptor.example;

import br.com.caelum.vraptor.Accepts;
import br.com.caelum.vraptor.interceptor.AcceptsWithAnnotations;

@AcceptsWithAnnotations(NotLogged.class)
public class InternalAndCustomAcceptsInterceptor {
	
	@Accepts
	public boolean accepts(){
		return true;
	}

}
