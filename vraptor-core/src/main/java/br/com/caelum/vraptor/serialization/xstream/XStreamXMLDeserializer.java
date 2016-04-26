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
package br.com.caelum.vraptor.serialization.xstream;

import java.io.InputStream;
import java.lang.reflect.Method;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import br.com.caelum.vraptor.controller.ControllerMethod;
import br.com.caelum.vraptor.http.Parameter;
import br.com.caelum.vraptor.http.ParameterNameProvider;
import br.com.caelum.vraptor.serialization.Deserializer;
import br.com.caelum.vraptor.serialization.Deserializes;

import com.thoughtworks.xstream.XStream;

/**
 * XStream based Xml Deserializer
 * @author Lucas Cavalcanti
 * @author Cecilia Fernandes
 * @author Guilherme Silveira
 * @author Rafael Viana
 * @since 3.0.2
 */
@RequestScoped
@Deserializes({"application/xml","xml", "text/xml"})
public class XStreamXMLDeserializer implements Deserializer {

	private final ParameterNameProvider provider;
	private final XStreamBuilder builder;

	/** 
	 * @deprecated CDI eyes only
	 */
	protected XStreamXMLDeserializer() {
		this(null, null);
	}

	@Inject
	public XStreamXMLDeserializer(ParameterNameProvider provider, XStreamBuilder builder) {
		this.provider = provider;
		this.builder = builder;
	}

	@Override
	public Object[] deserialize(InputStream inputStream, ControllerMethod method) {
		Method javaMethod = method.getMethod();
		Class<?>[] types = javaMethod.getParameterTypes();
		if (types.length == 0) {
			throw new IllegalArgumentException("Methods that consumes xml must receive just one argument: the xml root element");
		}
		XStream xStream = getConfiguredXStream(javaMethod, types);

		Object[] params = new Object[types.length];

		chooseParam(types, params, xStream.fromXML(inputStream));

		return params;
	}

	/**
	 * @return an xstream instance already configured.
	 */
	public XStream getConfiguredXStream(Method javaMethod, Class<?>[] types) {
		XStream xStream = builder.recursive().xmlInstance();

		xStream.processAnnotations(types);

		aliasParams(javaMethod, xStream);
		return xStream;
	}

	private static void chooseParam(Class<?>[] types, Object[] params, Object deserialized) {
		for (int i = 0; i < types.length; i++) {
			if (types[i].isInstance(deserialized)) {
				params[i] = deserialized;
			}
		}
	}

	private void aliasParams(Method method, XStream deserializer) {
		for (Parameter param : provider.parametersFor(method)) {
			deserializer.alias(param.getName(), param.getType());
		}
	}
}
