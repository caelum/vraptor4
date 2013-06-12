package br.com.caelum.vrapto4.test;

import javax.annotation.Priority;
import javax.enterprise.inject.Alternative;
import javax.inject.Inject;

import br.com.caelum.vraptor.http.FormatResolver;
import br.com.caelum.vraptor.view.DefaultPathResolver;

@Alternative @Priority(1000)
public class CustomPathResolver extends DefaultPathResolver{

	
	@Inject
	public CustomPathResolver(FormatResolver resolver) {
		super(resolver);
	}

	@Override
	protected String getPrefix() {
		return "/WEB-INF/paginas/";
	}
}
