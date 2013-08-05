package br.com.caelum.vrapto4.test;

import javax.annotation.Priority;
import javax.enterprise.inject.Alternative;
import javax.inject.Inject;
import javax.interceptor.Interceptor;

import br.com.caelum.vraptor4.http.FormatResolver;
import br.com.caelum.vraptor4.view.DefaultPathResolver;

@Alternative
@Priority(Interceptor.Priority.APPLICATION)
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
