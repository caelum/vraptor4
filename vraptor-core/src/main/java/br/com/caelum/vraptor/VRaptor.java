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

import static org.slf4j.LoggerFactory.getLogger;

import java.io.IOException;

import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.interceptor.Interceptor.Priority;
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

import br.com.caelum.vraptor.core.NewRequest;
import br.com.caelum.vraptor.core.StaticContentHandler;
import br.com.caelum.vraptor.events.EndRequest;
import br.com.caelum.vraptor.events.VRaptorInitialized;
import br.com.caelum.vraptor.http.EncodingHandler;
import br.com.caelum.vraptor.http.VRaptorRequest;
import br.com.caelum.vraptor.http.VRaptorResponse;
import br.com.caelum.vraptor.interceptor.ApplicationLogicException;

/**
 * VRaptor entry point.<br>
 * Provider configuration is supported through init parameter.
 *
 * @author Guilherme Silveira
 * @author Fabio Kung
 */
@WebFilter(filterName="vraptor", urlPatterns="/*", dispatcherTypes={DispatcherType.FORWARD, DispatcherType.REQUEST})
public class VRaptor implements Filter {

	public static final String VERSION = "4.0.0-RC2-SNAPSHOT";

	private final Logger logger = getLogger(VRaptor.class);


	private ServletContext servletContext;

	@Inject
	private StaticContentHandler staticHandler;

	@Inject
	private EncodingHandler encodingHandler;
	
	@Inject
	private Event<VRaptorInitialized> initializedEvent;

	@Inject
	private Event<NewRequest> newRequestEvent;
	
	@Inject
	private Event<EndRequest> endRequestEvent;

	@Override
	public void init(FilterConfig cfg) throws ServletException {
		servletContext = cfg.getServletContext();

		validateJavaEE7Environment();
		validateIfCdiIsFound();
		warnIfBeansXmlIsNotFound();

		initializedEvent.fire(new VRaptorInitialized(servletContext));
		
		logger.info("VRaptor {} successfuly initialized", VERSION);
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException,
			ServletException {

		validateServletEnvironment(req, res);

		final HttpServletRequest baseRequest = (HttpServletRequest) req;
		final HttpServletResponse baseResponse = (HttpServletResponse) res;

		if (staticHandler.requestingStaticFile(baseRequest)) {
			staticHandler.deferProcessingToContainer(chain, baseRequest, baseResponse);
		} else {
			logger.trace("VRaptor received a new request {}", req);

			VRaptorRequest mutableRequest = new VRaptorRequest(baseRequest);
			VRaptorResponse mutableResponse = new VRaptorResponse(baseResponse);

			final NewRequest request = new NewRequest(chain, mutableRequest, mutableResponse);

			try {
				encodingHandler.setEncoding(baseRequest, baseResponse);
				newRequestEvent.fire(request);
			} catch (ApplicationLogicException e) {
				// it is a business logic exception, we dont need to show
				// all interceptors stack trace
				throw new ServletException(e.getMessage(), e.getCause());
			}
			
			endRequestEvent.fire(new EndRequest());
			logger.debug("VRaptor ended the request");
		}
	}

	@Override
	public void destroy() {
		servletContext = null;
	}

	private void validateServletEnvironment(ServletRequest req, ServletResponse res) throws ServletException {
		if (!(req instanceof HttpServletRequest) || !(res instanceof HttpServletResponse)) {
			throw new ServletException("VRaptor must be run inside a Servlet environment. Portlets and others aren't supported.");
		}
	}

	private void warnIfBeansXmlIsNotFound() {
		if (servletContext.getRealPath("/WEB-INF/beans.xml") == null) {
			logger.warn("A beans.xml isn't found. Check if your beans.xml is properly located at /WEB-INF/beans.xml");
		}
	}

	private void validateJavaEE7Environment() throws ServletException {
		try {
			servletContext.getJspConfigDescriptor(); // check servlet 3
			Priority.class.toString(); // check CDI 1.1
		} catch (NoClassDefFoundError | java.lang.NoSuchMethodError e) {
			throw new ServletException("VRaptor only runs under Java EE 7 environment or Servlet Containers that "
					+ "supports Servlets 3 with CDI 1.1 jars.");
		}
	}

	private void validateIfCdiIsFound() throws ServletException {
		if (staticHandler == null) {
			throw new ServletException("Dependencies were not set. Do you have a Weld/CDI listener setup in your web.xml?");
		}
	}
}