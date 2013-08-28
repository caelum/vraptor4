package br.com.caelum.vraptor4.ioc.fixture;

import java.util.Set;

import javax.enterprise.context.ApplicationScoped;

import br.com.caelum.vraptor4.controller.HttpMethod;
import br.com.caelum.vraptor4.controller.MethodNotAllowedHandler;
import br.com.caelum.vraptor4.core.RequestInfo;

@ApplicationScoped
public class CustomMethodNotAllowedHandler implements MethodNotAllowedHandler {

	public void deny(RequestInfo request, Set<HttpMethod> allowedMethods) {
	}

}
