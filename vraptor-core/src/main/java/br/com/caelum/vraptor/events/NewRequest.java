package br.com.caelum.vraptor.events;

import br.com.caelum.vraptor.VRaptor;
import br.com.caelum.vraptor.core.RequestInfo;

/**
 * Event fired by {@link VRaptor} filter.
 *
 * @author Rodrigo Turini
 * @since 4.0
 */
public class NewRequest {

	private final RequestInfo request;

	public NewRequest(RequestInfo request) {
		this.request = request;
	}
	
	public RequestInfo getRequest() {
		return request;
	}

}