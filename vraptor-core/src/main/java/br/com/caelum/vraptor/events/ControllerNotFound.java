package br.com.caelum.vraptor.events;

import br.com.caelum.vraptor.controller.DefaultControllerNotFoundHandler;
import br.com.caelum.vraptor.core.RequestInfo;

/**
 * Event fired when a controller is not found 
 * in {@link DefaultControllerNotFoundHandler}
 * 
 * @author Chico Sokol
 */
public class ControllerNotFound {

	private RequestInfo request;

	public ControllerNotFound(RequestInfo request) {
		this.request = request;
	}
	
	public RequestInfo getRequest() {
		return request;
	}

}
