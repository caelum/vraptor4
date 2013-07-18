package br.com.caelum.vraptor.ioc.fixture;

import java.util.Set;

import br.com.caelum.vraptor4.core.RequestInfo;
import br.com.caelum.vraptor4.ioc.ApplicationScoped;
import br.com.caelum.vraptor4x.controller.HttpMethod;
import br.com.caelum.vraptor4x.controller.MethodNotAllowedHandler;

@ApplicationScoped
public class CustomMethodNotAllowedHandler implements MethodNotAllowedHandler {

	public void deny(RequestInfo request, Set<HttpMethod> allowedMethods) {
	}

}
