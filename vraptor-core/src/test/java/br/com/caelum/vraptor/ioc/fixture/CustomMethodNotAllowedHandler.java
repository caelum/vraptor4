package br.com.caelum.vraptor.ioc.fixture;

import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;

import br.com.caelum.vraptor.controller.HttpMethod;
import br.com.caelum.vraptor.controller.MethodNotAllowedHandler;
import br.com.caelum.vraptor.core.RequestInfo;

@ApplicationScoped
@Alternative
public class CustomMethodNotAllowedHandler implements MethodNotAllowedHandler {

	@Override
	public void deny(RequestInfo request, Set<HttpMethod> allowedMethods) {
	}
}