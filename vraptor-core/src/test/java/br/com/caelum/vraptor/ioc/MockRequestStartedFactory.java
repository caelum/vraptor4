package br.com.caelum.vraptor.ioc;

import javax.enterprise.inject.Specializes;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.com.caelum.vraptor.events.RequestStarted;
import br.com.caelum.vraptor.ioc.RequestStartedFactory;

@Specializes
class MockRequestStartedFactory extends RequestStartedFactory{

	public static final String PATTERN_TO_AVOID_VRAPTOR_STACK = "Some pattern, *.jsf perhaps?";

	@Override
	public RequestStarted createEvent(HttpServletRequest baseRequest, HttpServletResponse baseResponse,
			FilterChain chain) {

		if (PATTERN_TO_AVOID_VRAPTOR_STACK.equals(baseRequest.getRequestURI())) {
			return new AnotherFrameworkRequestStarted(baseRequest, baseResponse, chain);
		}

		return super.createEvent(baseRequest, baseResponse, chain);
	}
}
