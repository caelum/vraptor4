package br.com.caelum.vraptor.ioc;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.com.caelum.vraptor.events.RequestStarted;
import br.com.caelum.vraptor.http.MutableRequest;
import br.com.caelum.vraptor.http.MutableResponse;
import br.com.caelum.vraptor.http.VRaptorRequest;
import br.com.caelum.vraptor.http.VRaptorResponse;

public class AnotherFrameworkRequestStarted implements RequestStarted {

	private HttpServletRequest request;
	private HttpServletResponse response;
	private FilterChain chain;

	public AnotherFrameworkRequestStarted(HttpServletRequest request, HttpServletResponse response,
			FilterChain chain) {

		this.request = request;
		this.response = response;
		this.chain = chain;
	}

	@Override
	public FilterChain getChain() {
		return chain;
	}

	@Override
	public MutableRequest getRequest() {
		return new VRaptorRequest(request);
	}

	@Override
	public MutableResponse getResponse() {
		return new VRaptorResponse(response);
	}
}
