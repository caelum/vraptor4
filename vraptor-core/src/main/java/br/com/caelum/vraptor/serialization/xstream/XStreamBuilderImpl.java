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

import java.io.Writer;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import br.com.caelum.vraptor.interceptor.TypeNameExtractor;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.HierarchicalStreamDriver;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.json.JsonHierarchicalStreamDriver;
import com.thoughtworks.xstream.io.json.JsonWriter;

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

	private boolean indented;
	private boolean withoutRoot;

	@Inject
	public XStreamBuilderImpl(XStreamConverters converters, TypeNameExtractor extractor) {
		this.converters = converters;
		this.extractor = extractor;
	}

	@Override
	public XStream xmlInstance() {
		return configure(new VRaptorXStream(extractor));
	}

	protected static final String DEFAULT_NEW_LINE = "";
	protected static final char[] DEFAULT_LINE_INDENTER = {};

	protected static final String INDENTED_NEW_LINE = "\n";
	protected static final char[] INDENTED_LINE_INDENTER = { ' ', ' '};

	public XStream jsonInstance() {
		return configure(new VRaptorXStream(extractor, getHierarchicalStreamDriver()));
	}

	@Override
	public XStream configure(XStream xstream) {
		converters.registerComponents(xstream);
		return xstream;
	}

	/**
	  * You can override this method for configuring Driver before serialization
	  * @return configured hierarchical driver
	  */
	protected HierarchicalStreamDriver getHierarchicalStreamDriver() {
		final String newLine = (indented ? INDENTED_NEW_LINE : DEFAULT_NEW_LINE);
		final char[] lineIndenter = (indented ? INDENTED_LINE_INDENTER : DEFAULT_LINE_INDENTER);

		return new JsonHierarchicalStreamDriver() {
			@Override
			public HierarchicalStreamWriter createWriter(Writer writer) {
				if (withoutRoot) {
					return new JsonWriter(writer, lineIndenter, newLine, JsonWriter.DROP_ROOT_MODE);
				}

				return new JsonWriter(writer, lineIndenter, newLine);
			}
		};
	}

	@Override
	public XStreamBuilder indented() {
		indented = true;
		return this;
	}

	@Override
	public XStreamBuilder withoutRoot() {
		withoutRoot = true;
		return this;
	}

}