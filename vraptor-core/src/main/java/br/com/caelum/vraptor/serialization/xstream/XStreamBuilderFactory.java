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

import javax.enterprise.inject.Instance;

import br.com.caelum.vraptor.interceptor.DefaultTypeNameExtractor;
import br.com.caelum.vraptor.util.test.MockInstanceImpl;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.SingleValueConverter;

/**
 * A Simple builder factory for XStreamBuilder tests
 */
public class XStreamBuilderFactory {

	public static XStreamBuilder cleanInstance(Converter...converters) {
		Instance<Converter> convertersInst = new MockInstanceImpl<>(converters);
		Instance<SingleValueConverter> singleValueConverters = new MockInstanceImpl<>();
		XStreamConverters xStreamConverters = new XStreamConverters(convertersInst, singleValueConverters);
		return new XStreamBuilderImpl(xStreamConverters, new DefaultTypeNameExtractor());
	}
}