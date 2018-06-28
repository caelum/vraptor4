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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.inject.Instance;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;

import br.com.caelum.vraptor.core.DefaultReflectionProvider;
import br.com.caelum.vraptor.environment.Environment;
import br.com.caelum.vraptor.interceptor.DefaultTypeNameExtractor;
import br.com.caelum.vraptor.serialization.Serializee;
import br.com.caelum.vraptor.util.test.MockInstanceImpl;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.SingleValueConverter;

/**
 * testing the same cases as {@link XStreamXMLSerializationTest}
 * but using an arbitrary {@link XStream} implementation, not the {@link VRaptorXStream}.
 * @author lucascs
 *
 */
public class XStreamSerializerTest extends XStreamXMLSerializationTest {

	@Override
	@Before
	public void setup() throws Exception {
		stream = new ByteArrayOutputStream();
		environment = mock(Environment.class);

		HttpServletResponse response = mock(HttpServletResponse.class);
		when(response.getWriter()).thenReturn(new PrintWriter(stream));

		List<Converter> converters = new ArrayList<>();
		converters.add(new CalendarConverter());

		final DefaultTypeNameExtractor extractor = new DefaultTypeNameExtractor();

		Instance<Converter> convertersInst = new MockInstanceImpl<>(converters);
		Instance<SingleValueConverter> singleValueConverters = new MockInstanceImpl<>();
		XStreamConverters xStreamConverters = new XStreamConverters(convertersInst, singleValueConverters);
		serialization = new XStreamXMLSerialization(response, new XStreamBuilderImpl(xStreamConverters, extractor, 
				new Serializee(new DefaultReflectionProvider()), new DefaultReflectionProvider()), environment);
	}
}
