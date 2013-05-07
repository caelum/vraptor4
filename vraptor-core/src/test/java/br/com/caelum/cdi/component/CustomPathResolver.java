package br.com.caelum.cdi.component;

import javax.enterprise.inject.Alternative;

import br.com.caelum.vraptor.http.FormatResolver;
import br.com.caelum.vraptor.ioc.Component;
import br.com.caelum.vraptor.resource.ResourceMethod;
import br.com.caelum.vraptor.view.DefaultPathResolver;

@Alternative
@Component
public class CustomPathResolver extends DefaultPathResolver{

	public CustomPathResolver(FormatResolver resolver) {
		super(resolver);
	}

	public String pathFor(ResourceMethod method) {
		return "/vraptor/route";
	}
	
	

}
