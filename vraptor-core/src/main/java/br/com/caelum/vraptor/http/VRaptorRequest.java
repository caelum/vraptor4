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

package br.com.caelum.vraptor.http;

import static java.util.Collections.enumeration;
import static javax.servlet.RequestDispatcher.INCLUDE_REQUEST_URI;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.enterprise.inject.Vetoed;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A request capable of adding new parameters.
 *
 * @author guilherme silveira
 *
 */
@Vetoed
public class VRaptorRequest extends HttpServletRequestWrapper implements MutableRequest {

	private static final Logger logger = LoggerFactory.getLogger(VRaptorRequest.class);

	private final Map<String, String[]> extraParameters = new HashMap<>();

	public VRaptorRequest(HttpServletRequest request) {
		super(request);
	}

	@Override
	public String getParameter(String name) {
		if (extraParameters.containsKey(name)) {
			return extraParameters.get(name)[0];
		}
		return super.getParameter(name);
	}

	@Override
	public Enumeration<String> getParameterNames() {
		return enumeration(getParameterMap().keySet());
	}

	@Override
	public String[] getParameterValues(String name) {
		if (extraParameters.containsKey(name)) {
			return extraParameters.get(name);
		}
		return super.getParameterValues(name);
	}

	@Override
	public Map<String, String[]> getParameterMap() {
		Map<String, String[]> complete = new HashMap<>(super.getParameterMap());
		complete.putAll(extraParameters);
		return complete;
	}

	@Override
	public void setParameter(String key, String... value) {
		logger.debug("Setting {} with {}", key, value);
		extraParameters.put(key, value);
	}
	
	@Override
	public String getRequestedUri() {
		if (getAttribute(INCLUDE_REQUEST_URI) != null) {
			return (String) getAttribute(INCLUDE_REQUEST_URI);
		}
		String uri = getRelativeRequestURI(this);
		return uri.replaceFirst("(?i);jsessionid=.*$", "");
	}

	public static String getRelativeRequestURI(HttpServletRequest request) {
		if ("/".equals(request.getContextPath())) {
			return request.getRequestURI();
		}
		return request.getRequestURI().substring(request.getContextPath().length());
	}

	@Override
	public String toString() {
		return String.format("[VRaptorRequest %s]", this.getRequest());
	}
}
