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

import java.util.Arrays;
import java.util.Collections;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import br.com.caelum.vraptor.interceptor.DefaultTypeNameExtractor;
import br.com.caelum.vraptor.interceptor.TypeNameExtractor;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.SingleValueConverter;

/**
 * Implementation of default XStream configuration
 *
 * @author Rafael Viana
 * @since 3.4.0
 */
@Dependent
public class XStreamBuilderImpl implements XStreamBuilder {

	private final XStreamConverters converters;
	private final TypeNameExtractor extractor;

	@Inject
	public XStreamBuilderImpl(XStreamConverters converters, TypeNameExtractor extractor) {
		this.converters = converters;
		this.extractor = extractor;
	}

	public static XStreamBuilder cleanInstance(Converter...converters) {
		return new XStreamBuilderImpl(
				new XStreamConverters(Arrays.asList(converters), Collections.<SingleValueConverter>emptyList()),
				new DefaultTypeNameExtractor());
	}

	public XStream xmlInstance() {
		return configure(new VRaptorXStream(extractor));
	}

	public XStream configure(XStream xstream) {
		converters.registerComponents(xstream);
		return xstream;
	}
}