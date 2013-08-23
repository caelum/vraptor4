package br.com.caelum.vraptor4.ioc.fixture;

import java.util.Set;

import br.com.caelum.vraptor4.controller.HttpMethod;
import br.com.caelum.vraptor4.controller.MethodNotAllowedHandler;
import br.com.caelum.vraptor4.core.RequestInfo;
import br.com.caelum.vraptor4.ioc.ApplicationScoped;

@ApplicationScoped
public class CustomMethodNotAllowedHandler implements MethodNotAllowedHandler {

	public void deny(RequestInfo request, Set<HttpMethod> allowedMethods) {
	}

}
