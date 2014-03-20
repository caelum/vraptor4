package br.com.caelum.vraptor.ioc.fixture;

import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;

import br.com.caelum.vraptor.controller.HttpMethod;
import br.com.caelum.vraptor.controller.MethodNotAllowedHandler;
import br.com.caelum.vraptor.http.MutableRequest;
import br.com.caelum.vraptor.http.MutableResponse;

@ApplicationScoped
@Alternative
public class CustomMethodNotAllowedHandler implements MethodNotAllowedHandler {

	@Override
	public void deny(MutableRequest request, MutableResponse response, Set<HttpMethod> allowedMethods) {
		
	}
}