package br.com.caelum.vraptor.events;

import javax.servlet.ServletContext;

public class VRaptorInitialized {

	private final ServletContext servletContext;

	public VRaptorInitialized(ServletContext servletContext) {
		this.servletContext = servletContext;
	}
	
	public ServletContext getServletContext() {
		return servletContext;
	}
}
