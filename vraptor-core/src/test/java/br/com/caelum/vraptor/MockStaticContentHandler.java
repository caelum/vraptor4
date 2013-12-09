package br.com.caelum.vraptor;

import java.io.IOException;
import java.net.MalformedURLException;

import javax.enterprise.inject.Specializes;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.com.caelum.vraptor.core.DefaultStaticContentHandler;

@SuppressWarnings("deprecation")
@Specializes
class MockStaticContentHandler extends DefaultStaticContentHandler {

	private boolean deferProcessingToContainerCalled;

	@Override
	public boolean requestingStaticFile(HttpServletRequest request) throws MalformedURLException {
		return true;
	}

	@Override
	public void deferProcessingToContainer(FilterChain filterChain, HttpServletRequest request,
			HttpServletResponse response) throws IOException, ServletException {
		this.deferProcessingToContainerCalled = true;
	}

	public boolean isDeferProcessingToContainerCalled() {
		return deferProcessingToContainerCalled;
	}
}