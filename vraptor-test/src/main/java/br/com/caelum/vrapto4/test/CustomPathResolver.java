package br.com.caelum.vrapto4.test;

import javax.enterprise.inject.Specializes;
import javax.inject.Inject;

import br.com.caelum.vraptor4.http.FormatResolver;
import br.com.caelum.vraptor4.view.DefaultPathResolver;

@Specializes
public class CustomPathResolver extends DefaultPathResolver {

	@Inject
	public CustomPathResolver(FormatResolver resolver) {
		super(resolver);
	}

	@Override
	protected String getPrefix() {
		return "/WEB-INF/paginas/";
	}
}
