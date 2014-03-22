package br.com.caelum.vraptor.events;

import br.com.caelum.vraptor.http.MutableRequest;
import br.com.caelum.vraptor.http.MutableResponse;

public class EndRequest {

	private final MutableRequest request;
	private final MutableResponse response;

	public EndRequest(MutableRequest request, MutableResponse response) {
		this.request = request;
		this.response = response;
	}

	public MutableRequest getRequest() {
		return request;
	}

	public MutableResponse getResponse() {
		return response;
	}

}
