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

package br.com.caelum.vraptor.view;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor.controller.ControllerMethod;
import br.com.caelum.vraptor.http.FormatResolver;

/**
 * The default vraptor3 path resolver uses the type and method name as
 * "/typeName/methodName.jsp".
 *
 * @author Guilherme Silveira
 * @author Sérgio Lopes
 * @author Jonas Abreu
 */
@RequestScoped
public class DefaultPathResolver implements PathResolver {

	private static final Logger logger = LoggerFactory.getLogger(DefaultPathResolver.class);
	private final FormatResolver resolver;
	
	/** 
	 * @deprecated CDI eyes only
	 */
	protected DefaultPathResolver() {
		this(null);
	}

	@Inject
	public DefaultPathResolver(FormatResolver resolver) {
		this.resolver = resolver;
	}
	
	@Override
	public String pathFor(ControllerMethod method) {
		logger.debug("Resolving path for {}", method);
		String format = resolver.getAcceptFormat();

		String suffix = "";
		if (format != null && !format.equals("html")) {
			suffix = "." + format;
		}
		
		String name = method.getController().getType().getSimpleName();
		String folderName = extractControllerFromName(name);
		String path = getPrefix() + folderName + "/" + method.getMethod().getName() + suffix + "." + getExtension();

		logger.debug("Returning path {} for {}", path, method);
		return path;
	}

	protected String getPrefix() {
		return "/WEB-INF/jsp/";
	}

	protected String getExtension() {
		return "jsp";
	}

	protected String extractControllerFromName(String baseName) {
		baseName = lowerFirstCharacter(baseName);
		
		if (baseName.endsWith("Controller")) {
			return baseName.substring(0, baseName.lastIndexOf("Controller"));
		}
		
		return baseName;
	}

	private static String lowerFirstCharacter(String baseName) {
		return baseName.toLowerCase().substring(0, 1) + baseName.substring(1, baseName.length());
	}
}
