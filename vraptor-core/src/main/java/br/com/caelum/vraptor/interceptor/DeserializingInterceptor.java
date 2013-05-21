/***
 * Copyright (c) 2009 Caelum - www.caelum.com.br/opensource All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package br.com.caelum.vraptor.interceptor;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor.Consumes;
import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.Intercepts;
import br.com.caelum.vraptor.Lazy;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.core.MethodInfo;
import br.com.caelum.vraptor.deserialization.Deserializer;
import br.com.caelum.vraptor.deserialization.Deserializers;
import br.com.caelum.vraptor.ioc.Container;
import br.com.caelum.vraptor.view.Status;
import br.com.caelum.vraptor4.controller.ControllerMethod;

/**
 * Important: this interceptor must be after the {@link ParametersInstantiatorInterceptor}
 *
 * @author Lucas Cavalcanti
 * @author Rafael Ferreira
 */
@Intercepts(after=ParametersInstantiatorInterceptor.class, before=ExecuteMethodInterceptor.class)
@Lazy
public class DeserializingInterceptor implements Interceptor {
	private HttpServletRequest request;
	private Deserializers deserializers;
	private MethodInfo methodInfo;
	private Container container;
	private Status status;

	private static final Logger logger = LoggerFactory.getLogger(DeserializingInterceptor.class);

	@Deprecated
	public DeserializingInterceptor() {
	}
	
	@Inject
	public DeserializingInterceptor(HttpServletRequest servletRequest, Deserializers deserializers,
			MethodInfo methodInfo, Container container, Status status) {
		this.request = servletRequest;
		this.deserializers = deserializers;
		this.methodInfo = methodInfo;
		this.container = container;
		this.status = status;
	}
	
	public boolean accepts(ControllerMethod method) {
		return method.containsAnnotation(Consumes.class);
	}

	public void intercept(InterceptorStack stack, ControllerMethod method, Object resourceInstance) throws InterceptionException {
		Consumes consumesAnnotation = method.getMethod().getAnnotation(Consumes.class);
		List<String> supported =  Arrays.asList(consumesAnnotation.value());

		String contentType = mime(request.getContentType());
		if (!supported.isEmpty() && !supported.contains(contentType)) {
			unsupported(String.format("Request with media type [%s]. Expecting one of %s.",
					contentType, supported));
			return;
		}

		try {
			Deserializer deserializer = deserializers.deserializerFor(contentType, container);
			if (deserializer == null) {
				unsupported(String.format("Unable to handle media type [%s]: no deserializer found.", contentType));
				return;
			}

			Object[] deserialized = deserializer.deserialize(request.getInputStream(), method);
			Object[] parameters = methodInfo.getParameters();

			logger.debug("Deserialized parameters for {} are {} ", method, deserialized);

			// TODO: a new array should be created and then a call to setParameters
			// setting methodInfo.getParameters() works only because we dont (yet)
			// return a defensive copy
			for (int i = 0; i < deserialized.length; i++) {
				if (deserialized[i] != null) {
					parameters[i] = deserialized[i];
				}
			}

			stack.next(method, resourceInstance);
		} catch (IOException e) {
			throw new InterceptionException(e);
		}

	}

	private String mime(String contentType) {
		if (contentType.contains(";")) {
			return contentType.split(";")[0];
		}
		return contentType;
	}

	private void unsupported(String message) {
		this.status.unsupportedMediaType(message);
	}

}
