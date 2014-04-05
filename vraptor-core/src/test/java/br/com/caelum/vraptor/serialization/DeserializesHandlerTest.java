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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.controller.DefaultBeanClass;
import br.com.caelum.vraptor.serialization.Deserializer;
import br.com.caelum.vraptor.serialization.Deserializers;
import br.com.caelum.vraptor.serialization.DeserializesHandler;

public class DeserializesHandlerTest {

	private DeserializesHandler handler;
	private Deserializers deserializers;

	@Before
	public void setUp() throws Exception {
		deserializers = mock(Deserializers.class);
		handler = new DeserializesHandler(deserializers);
	}

	static interface MyDeserializer extends Deserializer{}
	static interface NotADeserializer{}

	@Test(expected=IllegalArgumentException.class)
	public void shouldThrowExceptionWhenTypeIsNotADeserializer() throws Exception {
		handler.handle(new DefaultBeanClass(NotADeserializer.class));
	}

	@Test
	public void shouldRegisterTypesOnDeserializers() throws Exception {
		handler.handle(new DefaultBeanClass(MyDeserializer.class));
		verify(deserializers).register(MyDeserializer.class);
	}
}