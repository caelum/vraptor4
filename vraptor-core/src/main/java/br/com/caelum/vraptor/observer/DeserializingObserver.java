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
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;

import br.com.caelum.vraptor.Consumes;
import br.com.caelum.vraptor.controller.ControllerMethod;
import br.com.caelum.vraptor.core.MethodInfo;
import br.com.caelum.vraptor.events.InterceptorsReady;
import br.com.caelum.vraptor.events.MethodReady;
import br.com.caelum.vraptor.ioc.Container;
import br.com.caelum.vraptor.serialization.Deserializer;
import br.com.caelum.vraptor.serialization.Deserializers;
import br.com.caelum.vraptor.view.Status;

/**
 * <strong>Important</strong>: this class must observe {@link MethodReady}
 * because it is fired just before {@link ExecuteMethod} execution
 *
 * @author Lucas Cavalcanti
 * @author Rafael Ferreira
 * @author Rodrigo Turini
 */
@ApplicationScoped
public class DeserializingObserver {

	private final Deserializers deserializers;
	private final Container container;

	private static final Logger logger = getLogger(DeserializingObserver.class);

	/**
	 * @deprecated CDI eyes only
	 */
	protected DeserializingObserver() {
		this(null, null);
	}

	@Inject
	public DeserializingObserver(Deserializers deserializers, Container container) {
		this.deserializers = deserializers;
		this.container = container;
	}

	public void deserializes(@Observes InterceptorsReady event, HttpServletRequest request,
			MethodInfo methodInfo, Status status) throws IOException {

		ControllerMethod method = event.getControllerMethod();

		if (!method.containsAnnotation(Consumes.class)) return;

		List<String> supported =  asList(method.getMethod().getAnnotation(Consumes.class).value());

		if(request.getContentType() == null) {
			logger.warn("Request does not have Content-Type and parameters will be not deserialized");
			return;
		}
		
		String contentType = mime(request.getContentType());
		if (!supported.isEmpty() && !supported.contains(contentType)) {
			unsupported("Request with media type [%s]. Expecting one of %s.", status, contentType, supported);
			return;
		}

		Deserializer deserializer = deserializers.deserializerFor(contentType, container);
		if (deserializer == null) {
			unsupported("Unable to handle media type [%s]: no deserializer found.", status, contentType);
			return;
		}

		Object[] deserialized = deserializer.deserialize(request.getInputStream(), method);
		logger.debug("Deserialized parameters for {} are {} ", method, deserialized);

		for (int i = 0; i < deserialized.length; i++) {
			if (deserialized[i] != null) {
				methodInfo.setParameter(i, deserialized[i]);
			}
		}
	}

	private static String mime(String contentType) {
		if (contentType.contains(";")) {
			return contentType.split(";")[0];
		}
		return contentType;
	}

	private void unsupported(String message, Status status, Object... params) {
		status.unsupportedMediaType(String.format(message, params));
	}
}
