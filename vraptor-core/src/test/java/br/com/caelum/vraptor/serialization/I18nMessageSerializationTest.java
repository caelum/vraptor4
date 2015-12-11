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
package br.com.caelum.vraptor.serialization;

import static br.com.caelum.vraptor.view.Results.json;
import static br.com.caelum.vraptor.view.Results.xml;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;

import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSerializer;

import br.com.caelum.vraptor.core.DefaultReflectionProvider;
import br.com.caelum.vraptor.environment.Environment;
import br.com.caelum.vraptor.interceptor.DefaultTypeNameExtractor;
import br.com.caelum.vraptor.ioc.Container;
import br.com.caelum.vraptor.serialization.gson.CalendarGsonConverter;
import br.com.caelum.vraptor.serialization.gson.GsonBuilderWrapper;
import br.com.caelum.vraptor.serialization.gson.GsonJSONSerialization;
import br.com.caelum.vraptor.serialization.gson.GsonSerializerBuilder;
import br.com.caelum.vraptor.serialization.gson.MessageGsonConverter;
import br.com.caelum.vraptor.serialization.xstream.MessageConverter;
import br.com.caelum.vraptor.serialization.xstream.XStreamBuilder;
import br.com.caelum.vraptor.serialization.xstream.XStreamBuilderImpl;
import br.com.caelum.vraptor.serialization.xstream.XStreamXMLSerialization;
import br.com.caelum.vraptor.util.test.MockInstanceImpl;
import br.com.caelum.vraptor.validator.SingletonResourceBundle;

public class I18nMessageSerializationTest {
	
	private I18nMessageSerialization serialization;
	private ByteArrayOutputStream stream;
	private PrintWriter writer;

	@Before
	public void setup() throws Exception {
		stream = new ByteArrayOutputStream();
		
		Environment environment = mock(Environment.class);

		HttpServletResponse response = mock(HttpServletResponse.class);
		writer = new PrintWriter(stream);
		when(response.getWriter()).thenReturn(writer);
		DefaultTypeNameExtractor extractor = new DefaultTypeNameExtractor();
		XStreamBuilder builder = XStreamBuilderImpl.cleanInstance(new MessageConverter());
		XStreamXMLSerialization xmlSerialization = new XStreamXMLSerialization(response, builder, environment);

		List<JsonSerializer<?>> jsonSerializers = new ArrayList<>();
		List<JsonDeserializer<?>> jsonDeserializers = new ArrayList<>();
		jsonSerializers.add(new CalendarGsonConverter());
		jsonSerializers.add(new MessageGsonConverter());

		GsonSerializerBuilder gsonBuilder =  new GsonBuilderWrapper(new MockInstanceImpl<>(jsonSerializers), 
				new MockInstanceImpl<>(jsonDeserializers), new Serializee(new DefaultReflectionProvider()), new DefaultReflectionProvider());
		GsonJSONSerialization jsonSerialization = new GsonJSONSerialization(response, extractor, gsonBuilder, environment,
				new DefaultReflectionProvider());

		Container container = mock(Container.class);
		when(container.instanceFor(JSONSerialization.class)).thenReturn(jsonSerialization);
		when(container.instanceFor(XMLSerialization.class)).thenReturn(xmlSerialization);

		ResourceBundle bundle = new SingletonResourceBundle("message.cat", "Just another {0} in {1}");
		serialization = new I18nMessageSerialization(container , bundle);
	}

	@Test
	public void shouldCallJsonSerialization() {
		String expectedResult = "{\"message\":{\"category\":\"success\",\"message\":\"Just another {0} in {1}\"}}";
		serialization.from("success", "message.cat").as(json());
		writer.flush();
		
		assertThat(result(), is(equalTo(expectedResult)));
	}

	@Test
	public void shouldCallXStreamXmlSerialization() {
		String expectedResult = "<message><message>Just another {0} in {1}</message><category>success</category></message>";
		serialization.from("success", "message.cat").as(xml());
		assertThat(result(), is(equalTo(expectedResult)));
	}

	@Test
	public void shouldCallXStreamSerializationWithParams() {
		String expectedResult = "<message><message>Just another object in memory</message><category>success</category></message>";
		Object[] params = {"object", "memory"};
		serialization.from("success", "message.cat", params).as(xml());
		assertThat(result(), is(equalTo(expectedResult)));
	}


	private String result() {
		return new String(stream.toByteArray());
	}

}
