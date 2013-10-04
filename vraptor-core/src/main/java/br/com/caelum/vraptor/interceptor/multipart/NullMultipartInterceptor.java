/***
 * Copyright (c) 2009 Caelum - www.caelum.com.br/opensource
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package br.com.caelum.vraptor.interceptor.multipart;

import static com.google.common.base.Strings.nullToEmpty;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.controller.ControllerMethod;
import br.com.caelum.vraptor.core.InterceptorStack;

/**
 * A null implementation of {@link MultipartInterceptor}.
 *
 * @author Otávio Scherer Garcia
 * @since 3.1.3
 */
@RequestScoped
public class NullMultipartInterceptor implements MultipartInterceptor {
	
	private static final Logger logger = LoggerFactory.getLogger(NullMultipartInterceptor.class);
	
	private HttpServletRequest request;

	@Deprecated // CDI eyes only
	public NullMultipartInterceptor() {
	}

	@Inject
	public NullMultipartInterceptor(HttpServletRequest request) {
		this.request = request;
	}

	/**
	 * Only accepts multipart requests.
	 */
	public boolean accepts(ControllerMethod method) {
		return request.getMethod().toUpperCase().equals("POST") && 
				nullToEmpty(request.getContentType()).startsWith("multipart/form-data");
	}

	public void intercept(InterceptorStack stack, ControllerMethod method, Object controllerInstance)
		throws InterceptionException {
		logger.warn("There is no file upload handlers registered. If you are willing to upload a file, please "
				+ "add the commons-fileupload in your classpath");
		stack.next(method, controllerInstance);
	}
}
