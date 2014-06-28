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

import javax.enterprise.inject.Vetoed;

import br.com.caelum.vraptor.interceptor.TypeNameExtractor;

import com.google.common.base.Supplier;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.reflection.PureJavaReflectionProvider;
import com.thoughtworks.xstream.io.HierarchicalStreamDriver;
import com.thoughtworks.xstream.mapper.MapperWrapper;

@Vetoed
public final class VRaptorXStream extends XStream {
	private final TypeNameExtractor extractor;
	private VRaptorClassMapper vraptorMapper;

	{setMode(NO_REFERENCES);}

	public VRaptorXStream(TypeNameExtractor extractor) {
		super(new PureJavaReflectionProvider());
		this.extractor = extractor;
	}
	
	public VRaptorXStream(TypeNameExtractor extractor, HierarchicalStreamDriver hierarchicalStreamDriver) {
		super(new PureJavaReflectionProvider(),hierarchicalStreamDriver);
		this.extractor = extractor;
	}

	@Override
	protected MapperWrapper wrapMapper(MapperWrapper next) {
		
		vraptorMapper = new VRaptorClassMapper(next,
		/* this method is called in the super constructor, so we cannot use instance variables, so we're
		 * using this 'lazy' get */
		new Supplier<TypeNameExtractor>() {
			@Override
			public TypeNameExtractor get() {
				return extractor;
			}
		});
		return vraptorMapper;
	}
	
	public VRaptorClassMapper getVRaptorMapper() {
		return vraptorMapper;
	}
}