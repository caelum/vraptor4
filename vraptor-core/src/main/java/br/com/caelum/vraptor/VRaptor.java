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
import java.net.MalformedURLException;
import java.net.URL;

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

import br.com.caelum.vraptor.core.StaticContentHandler;
import br.com.caelum.vraptor.events.RequestStarted;
import br.com.caelum.vraptor.events.VRaptorInitialized;
import br.com.caelum.vraptor.http.EncodingHandler;
import br.com.caelum.vraptor.interceptor.ApplicationLogicException;
import br.com.caelum.vraptor.ioc.RequestStartedFactory;
import br.com.caelum.vraptor.ioc.cdi.CDIRequestFactories;

/**
 * VRaptor entry point.<br>
 * Provider configuration is supported through init parameter.
 *
 * @author Guilherme Silveira
 * @author Fabio Kung
 */
@WebFilter(filterName="vraptor", urlPatterns="/*", dispatcherTypes={DispatcherType.FORWARD, DispatcherType.REQUEST}, asyncSupported=true)
public class VRaptor implements Filter {

	public static final String VERSION = "4.3.0-beta-3-SNAPSHOT";

	private final Logger logger = getLogger(VRaptor.class);


	private ServletContext servletContext;

	@Inject
	private StaticContentHandler staticHandler;

	@Inject
	private EncodingHandler encodingHandler;

	@Inject
	private Event<VRaptorInitialized> initializedEvent;

	@Inject
	private Event<RequestStarted> requestStartedEvent;

	@Inject
	private RequestStartedFactory requestStartedFactory;

	@Inject
	private CDIRequestFactories cdiRequestFactories;

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
		
		if (isWebsocketRequest(baseRequest)) {
			chain.doFilter(req, res);
			return;
		}
		
		if (staticHandler.requestingStaticFile(baseRequest)) {
			staticHandler.deferProcessingToContainer(chain, baseRequest, baseResponse);
		} else {
			logger.trace("VRaptor received a new request {}", req);

			try {
				encodingHandler.setEncoding(baseRequest, baseResponse);
				RequestStarted requestStarted = requestStartedFactory.createEvent(baseRequest, baseResponse, chain);

				cdiRequestFactories.setRequest(requestStarted);
				requestStartedEvent.fire(requestStarted);
			} catch (ApplicationLogicException e) {
				// it is a business logic exception, we dont need to show
				// all interceptors stack trace
				throw new ServletException(e.getMessage(), e.getCause());
			}

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

	private void warnIfBeansXmlIsNotFound() throws ServletException {
		
		URL webInfFile = getResource("/WEB-INF/beans.xml");
		URL metaInfFile = getResource("/WEB-INF/classes/META-INF/beans.xml");

		if (webInfFile == null && metaInfFile == null) {
			logger.warn("A beans.xml isn't found. Check if is properly located at "
					+ "/WEB-INF/beans.xml or /WEB-INF/classes/META-INF/beans.xml");
		}
	}

	private URL getResource(String path) throws ServletException {
		try {
			return servletContext.getResource(path);
		} catch (MalformedURLException e) {
			logger.error("Something went wrong when trying to locate a beans.xml file", e);
			return null;
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
	
	/**
	 * According to the Websocket spec (https://tools.ietf.org/html/rfc6455): The WebSocket Protocol 
	 * 5. The request MUST contain an |Upgrade| header field whose value MUST include the "websocket" keyword.
	 */
	private boolean isWebsocketRequest(HttpServletRequest request) {
		String upgradeHeader = request.getHeader("Upgrade");
		return upgradeHeader != null && upgradeHeader.toLowerCase().contains("websocket");
	}
	
}
