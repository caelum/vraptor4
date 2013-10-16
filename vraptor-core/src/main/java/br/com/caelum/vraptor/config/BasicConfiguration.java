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

package br.com.caelum.vraptor.config;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.servlet.ServletContext;

/**
 * VRaptors servlet context init parameter configuration reader.
 *
 * @author Guilherme Silveira
 */
@ApplicationScoped
public class BasicConfiguration {

	/**
	 * context parameter that represents application character encoding
	 */
	public static final String ENCODING = "br.com.caelum.vraptor.encoding";

	private ServletContext servletContext;
	
	/** @Deprecated CDI eyes only */
	protected BasicConfiguration() {}

	@Inject
	public BasicConfiguration(ServletContext servletContext) {
		this.servletContext = servletContext;
	}

	public String getEncoding() {
		return servletContext.getInitParameter(ENCODING);
	}
}
