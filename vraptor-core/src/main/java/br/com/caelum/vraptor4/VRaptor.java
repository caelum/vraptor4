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

package br.com.caelum.vraptor4;

import java.io.IOException;

import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;

import br.com.caelum.vraptor4.core.Execution;
import br.com.caelum.vraptor4.core.RequestExecution;
import br.com.caelum.vraptor4.core.RequestInfo;
import br.com.caelum.vraptor4.core.StaticContentHandler;
import br.com.caelum.vraptor4.events.VRaptorInitialized;
import br.com.caelum.vraptor4.http.EncodingHandler;
import br.com.caelum.vraptor4.http.VRaptorRequest;
import br.com.caelum.vraptor4.http.VRaptorResponse;
import br.com.caelum.vraptor4.interceptor.ApplicationLogicException;
import br.com.caelum.vraptor4.ioc.Container;
import br.com.caelum.vraptor4.ioc.ContainerProvider;

/**
 * VRaptor entry point.<br>
 * Provider configuration is supported through init parameter.
 * 
 * @author Guilherme Silveira
 * @author Fabio Kung
 */
public class VRaptor implements Filter {

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

			Execution<Object> execution = new Execution<Object>() {
				@Override
				public Object insideRequest(Container container) {
					container.instanceFor(EncodingHandler.class).setEncoding(baseRequest, baseResponse);
					container.instanceFor(RequestExecution.class).execute();
					return null;
				}
			};

			try {
				provider.provideForRequest(request, execution);
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
		servletContext = cfg.getServletContext();
		contextEvent.fire(servletContext);
		this.provider.start(servletContext);
		logger.info("VRaptor 4.0 successfuly initialized");
		initializedEvent.fire(new VRaptorInitialized());
	}

}