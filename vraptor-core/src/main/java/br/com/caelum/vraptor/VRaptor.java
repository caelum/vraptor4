/***
 * Copyright (c) 2009 Caelum - www.caelum.com.br/opensource
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package br.com.caelum.vraptor;

import java.io.IOException;

import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;

import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.core.RequestInfo;
import br.com.caelum.vraptor.core.StaticContentHandler;
import br.com.caelum.vraptor.events.VRaptorInitialized;
import br.com.caelum.vraptor.http.EncodingHandler;
import br.com.caelum.vraptor.http.VRaptorRequest;
import br.com.caelum.vraptor.http.VRaptorResponse;
import br.com.caelum.vraptor.interceptor.ApplicationLogicException;
import br.com.caelum.vraptor.ioc.ContainerProvider;

/**
 * VRaptor entry point.<br>
 * Provider configuration is supported through init parameter.
 *
 * @author Guilherme Silveira
 * @author Fabio Kung
 */
@WebFilter(filterName="vraptor", urlPatterns="/*", dispatcherTypes={DispatcherType.FORWARD, DispatcherType.REQUEST})
public class VRaptor implements Filter {
	
	private static final String VERSION = "4.0.0-beta-3";

	@Inject
	private ContainerProvider provider;

	@Inject
	private Event<ServletContext> contextEvent;

	@Inject
	private Event<VRaptorInitialized> initializedEvent;

	private ServletContext servletContext;

	@Inject
	private StaticContentHandler staticHandler;

	@Inject
	private EncodingHandler encodingHandler;
	
	@Inject
	private InterceptorStack stack;

	@Inject
	private Logger logger;

	@Override
	public void destroy() {
		servletContext = null;
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException,
			ServletException {

		if (!(req instanceof HttpServletRequest) || !(res instanceof HttpServletResponse)) {
			throw new ServletException(
					"VRaptor must be run inside a Servlet environment. Portlets and others aren't supported.");
		}

		final HttpServletRequest baseRequest = (HttpServletRequest) req;
		final HttpServletResponse baseResponse = (HttpServletResponse) res;

		if (staticHandler.requestingStaticFile(baseRequest)) {
			staticHandler.deferProcessingToContainer(chain, baseRequest, baseResponse);
		} else {
			logger.debug("VRaptor received a new request");
			logger.trace("Request: {}", req);

			VRaptorRequest mutableRequest = new VRaptorRequest(baseRequest);
			VRaptorResponse mutableResponse = new VRaptorResponse(baseResponse);

			final RequestInfo request = new RequestInfo(servletContext, chain, mutableRequest, mutableResponse);

			try {
				encodingHandler.setEncoding(baseRequest, baseResponse);
				provider.provideForRequest(request);
				stack.start();
				
			} catch (ApplicationLogicException e) {
				// it is a business logic exception, we dont need to show
				// all interceptors stack trace
				throw new ServletException(e.getMessage(), e.getCause());
			}
			logger.debug("VRaptor ended the request");
		}
	}

	@Override
	public void init(FilterConfig cfg) throws ServletException {
		if (this.provider == null) {
			throw new ServletException("Container Provider is null. " +
					"Do you have a Weld/CDI listener setup in your web.xml?");
		}
		servletContext = cfg.getServletContext();
		contextEvent.fire(servletContext);
		this.provider.start();
		logger.info("VRaptor {} successfuly initialized", VERSION);
		initializedEvent.fire(new VRaptorInitialized());
	}
}