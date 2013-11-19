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
package br.com.caelum.vraptor.observer;

import static java.util.Arrays.asList;
import static org.slf4j.LoggerFactory.getLogger;

import java.io.IOException;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;

import br.com.caelum.vraptor.Consumes;
import br.com.caelum.vraptor.controller.ControllerMethod;
import br.com.caelum.vraptor.core.MethodInfo;
import br.com.caelum.vraptor.deserialization.Deserializer;
import br.com.caelum.vraptor.deserialization.Deserializers;
import br.com.caelum.vraptor.events.ReadyToExecuteMethod;
import br.com.caelum.vraptor.interceptor.ParametersInstantiatorInterceptor;
import br.com.caelum.vraptor.ioc.Container;
import br.com.caelum.vraptor.view.Status;

/**
 * <strong>Important</strong>: this class must observes {@link ReadyToExecuteMethod}
 * because it is fired just after {@link ParametersInstantiatorInterceptor} execution
 *
 * @author Lucas Cavalcanti
 * @author Rafael Ferreira
 * @author Rodrigo Turini
 */
@ApplicationScoped
public class DeserializingObserver {

	private final Instance<HttpServletRequest> request;
	private final Deserializers deserializers;
	private final Instance<MethodInfo> methodInfo;
	private final Container container;
	private final Instance<Status> status;

	private static final Logger logger = getLogger(DeserializingObserver.class);

	/**
	 * @deprecated CDI eyes only
	 */
	protected DeserializingObserver() {
		this(null, null, null, null, null);
	}

	@Inject
	public DeserializingObserver(Instance<HttpServletRequest> servletRequest, Deserializers deserializers,
			Instance<MethodInfo> methodInfo, Container container, Instance<Status> status) {

		this.request = servletRequest;
		this.deserializers = deserializers;
		this.methodInfo = methodInfo;
		this.container = container;
		this.status = status;
	}

	public void deserializes(@Observes ReadyToExecuteMethod event) throws IOException {

		ControllerMethod method = event.getControllerMethod();

		if (!method.containsAnnotation(Consumes.class)) return;

		List<String> supported =  asList(method.getMethod().getAnnotation(Consumes.class).value());

		HttpServletRequest httpServletRequest = request.get();

		String contentType = mime(httpServletRequest.getContentType());
		if (!supported.isEmpty() && !supported.contains(contentType)) {
			unsupported("Request with media type [%s]. Expecting one of %s.", contentType, supported);
			return;
		}

		Deserializer deserializer = deserializers.deserializerFor(contentType, container);
		if (deserializer == null) {
			unsupported("Unable to handle media type [%s]: no deserializer found.", contentType);
			return;
		}

		Object[] deserialized = deserializer.deserialize(request.get().getInputStream(), method);
		Object[] parameters = methodInfo.get().getParameters();

		logger.debug("Deserialized parameters for {} are {} ", method, deserialized);

		// TODO: a new array should be created and then a call to setParameters
		// setting methodInfo.getParameters() works only because we dont (yet)
		// return a defensive copy
		for (int i = 0; i < deserialized.length; i++) {
			if (deserialized[i] != null) {
				parameters[i] = deserialized[i];
			}
		}
	}

	private String mime(String contentType) {
		if (contentType.contains(";")) {
			return contentType.split(";")[0];
		}
		return contentType;
	}

	private void unsupported(String message, Object... params) {
		this.status.get().unsupportedMediaType(String.format(message, params));
	}
}