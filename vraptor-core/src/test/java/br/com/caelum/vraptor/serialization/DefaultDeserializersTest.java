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

import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.com.caelum.vraptor.ioc.Container;

public class DefaultDeserializersTest {

	@Rule
	public ExpectedException exception = ExpectedException.none();

	private Deserializers deserializers;
	@Mock private Container container;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		deserializers = new DefaultDeserializers();
	}

	@Test
	public void shouldThrowExceptionWhenThereIsNoDeserializerRegisteredForGivenContentType() throws Exception {
		assertNull(deserializers.deserializerFor("bogus content type", container));
	}

	static interface NotAnnotatedDeserializer extends Deserializer {}

	@Test
	public void allDeserializersMustBeAnnotatedWithDeserializes() throws Exception {
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("You must annotate your deserializers with @Deserializes");

		deserializers.register(NotAnnotatedDeserializer.class);
	}

	@Deserializes({"application/xml", "json"})
	static interface MyDeserializer extends Deserializer {}

	@Test
	public void shouldNotCallDeserializerIfItDoesntAcceptGivenContentType() throws Exception {
		deserializers.register(MyDeserializer.class);

		assertNull(deserializers.deserializerFor("image/jpeg", container));

		verify(container, never()).instanceFor(MyDeserializer.class);
	}

	@Test
	public void shouldUseTheDeserializerThatAcceptsTheGivenContentType() throws Exception {
		deserializers.register(MyDeserializer.class);

		deserializers.deserializerFor("application/xml", container);

		verify(container).instanceFor(MyDeserializer.class);
	}

	@Test
	public void shouldUseTheDeserializerThatAcceptsTheGivenContentTypeIfVendorized() throws Exception {
		deserializers.register(MyDeserializer.class);

		deserializers.deserializerFor("application/json", container);

		verify(container).instanceFor(MyDeserializer.class);
	}

	@Test
	public void shouldUseTheDeserializerThatAcceptsTheGivenContentTypeIfAtomizedAlike() throws Exception {
		deserializers.register(MyDeserializer.class);

		deserializers.deserializerFor("application/atom+json", container);

		verify(container).instanceFor(MyDeserializer.class);
	}
}
