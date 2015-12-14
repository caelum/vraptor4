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

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import br.com.caelum.vraptor.core.DefaultReflectionProvider;
import br.com.caelum.vraptor.core.ReflectionProvider;
import br.com.caelum.vraptor.interceptor.DefaultTypeNameExtractor;
import br.com.caelum.vraptor.interceptor.TypeNameExtractor;
import br.com.caelum.vraptor.serialization.Serializee;
import br.com.caelum.vraptor.util.test.MockInstanceImpl;

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
	private final Serializee serializee;
	private final ReflectionProvider reflectionProvider;

	private boolean indented;
	private boolean recursive;
	
	/** 
	 * @deprecated CDI eyes only
	 */
	protected XStreamBuilderImpl() {
		this(null, null, null, null);
	}

	@Inject
	public XStreamBuilderImpl(XStreamConverters converters, TypeNameExtractor extractor, Serializee serializee,
			ReflectionProvider reflectionProvider) {
		this.converters = converters;
		this.extractor = extractor;
		this.serializee = serializee;
		this.reflectionProvider = reflectionProvider;
	}

	public static XStreamBuilder cleanInstance(Converter...converters) {
		Instance<Converter> convertersInst = new MockInstanceImpl<>(converters);
		Instance<SingleValueConverter> singleValueConverters = new MockInstanceImpl<>();
		XStreamConverters xStreamConverters = new XStreamConverters(convertersInst, singleValueConverters);
		return new XStreamBuilderImpl(xStreamConverters, new DefaultTypeNameExtractor(), 
				new Serializee(new DefaultReflectionProvider()), new DefaultReflectionProvider());
	}
	
	@Override
	public XStream xmlInstance() {
		VRaptorXStream xstream = new VRaptorXStream(extractor, serializee, reflectionProvider);
		serializee.setRecursive(recursive);
		return configure(xstream);
	}

	@Override
	public XStream configure(XStream xstream) {
		converters.registerComponents(xstream);
		return xstream;
	}

	@Override
	public XStreamBuilder indented() {
		indented = true;
		return this;
	}
	
	boolean isIndented() {
		return indented;
	}
	
	public boolean isRecursive() {
		return recursive;
	}
	
	@Override
	public XStreamBuilder recursive() {
		recursive = true;
		return this;
	}
}
