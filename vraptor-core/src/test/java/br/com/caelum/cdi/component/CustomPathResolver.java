package br.com.caelum.cdi.component;

import javax.enterprise.inject.Alternative;

import br.com.caelum.vraptor.http.FormatResolver;
import br.com.caelum.vraptor.view.DefaultPathResolver;
import br.com.caelum.vraptor4.controller.ControllerMethod;

@Alternative
public class CustomPathResolver extends DefaultPathResolver{

	//CDI eyes only
	@Deprecated
	public CustomPathResolver() {
	}
	
	public CustomPathResolver(FormatResolver resolver) {
		super(resolver);
	}

	@Override
	public String pathFor(ControllerMethod method) {
		return "/vraptor/route";
	}
	
	

}
