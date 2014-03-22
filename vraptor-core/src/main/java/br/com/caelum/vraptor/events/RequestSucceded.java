package br.com.caelum.vraptor.events;

import br.com.caelum.vraptor.http.MutableRequest;
import br.com.caelum.vraptor.http.MutableResponse;

public class RequestSucceded {

	private final MutableRequest request;
	private final MutableResponse response;

	public RequestSucceded(MutableRequest request, MutableResponse response) {
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
