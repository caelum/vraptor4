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
package br.com.caelum.vraptor.observer;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.com.caelum.vraptor.Consumes;
import br.com.caelum.vraptor.controller.ControllerMethod;
import br.com.caelum.vraptor.controller.DefaultControllerMethod;
import br.com.caelum.vraptor.core.MethodInfo;
import br.com.caelum.vraptor.events.InterceptorsReady;
import br.com.caelum.vraptor.http.ParanamerNameProvider;
import br.com.caelum.vraptor.ioc.Container;
import br.com.caelum.vraptor.serialization.Deserializer;
import br.com.caelum.vraptor.serialization.Deserializers;
import br.com.caelum.vraptor.view.Status;


public class DeserializingObserverTest {
	private DeserializingObserver observer;
	private ControllerMethod consumeXml;
	private ControllerMethod doesntConsume;
	private MethodInfo methodInfo;

	@Mock private HttpServletRequest request;
	@Mock Deserializers deserializers;
	@Mock Container container;
	@Mock private Status status;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		methodInfo = new MethodInfo(new ParanamerNameProvider());

		observer = new DeserializingObserver(deserializers, container);
		consumeXml = new DefaultControllerMethod(null, DummyResource.class.getDeclaredMethod("consumeXml", String.class, String.class));
		doesntConsume = new DefaultControllerMethod(null, DummyResource.class.getDeclaredMethod("doesntConsume"));
	}


	static class DummyResource {
		@Consumes("application/xml")
		public void consumeXml(String a, String b) {}

		@Consumes()
		public void consumesAnything(String a, String b) {}

		public void doesntConsume() {}
	}

	@Test
	public void shouldOnlyAcceptMethodsWithConsumesAnnotation() throws Exception {
		observer.deserializes(new InterceptorsReady(doesntConsume), request, methodInfo, status);
		verifyZeroInteractions(request);
	}

	@Test
	public void willSetHttpStatusCode415IfTheControllerMethodDoesNotSupportTheGivenMediaTypes() throws Exception {
		when(request.getContentType()).thenReturn("image/jpeg");

		observer.deserializes(new InterceptorsReady(consumeXml), request, methodInfo, status);

		verify(status).unsupportedMediaType("Request with media type [image/jpeg]. Expecting one of [application/xml].");
	}

	@Test
	public void willSetHttpStatusCode415IfThereIsNoDeserializerButIsAccepted() throws Exception {
		when(request.getContentType()).thenReturn("application/xml");
		when(deserializers.deserializerFor("application/xml", container)).thenReturn(null);

		observer.deserializes(new InterceptorsReady(consumeXml), request, methodInfo, status);

		verify(status).unsupportedMediaType("Unable to handle media type [application/xml]: no deserializer found.");
	}

	@Test
	public void willSetMethodParametersWithDeserializationAndContinueStackAfterDeserialization() throws Exception {
		final Deserializer deserializer = mock(Deserializer.class);

		methodInfo.setControllerMethod(consumeXml);

		when(request.getContentType()).thenReturn("application/xml");
		when(deserializer.deserialize(null, consumeXml)).thenReturn(new Object[] {"abc", "def"});
		when(deserializers.deserializerFor("application/xml", container)).thenReturn(deserializer);

		observer.deserializes(new InterceptorsReady(consumeXml), request, methodInfo, status);

		assertEquals(methodInfo.getValuedParameters()[0].getValue(), "abc");
		assertEquals(methodInfo.getValuedParameters()[1].getValue(), "def");
	}

	@Test
	public void willSetMethodParametersWithDeserializationEvenIfTheContentTypeHasCharsetDeclaration() throws Exception {
		final Deserializer deserializer = mock(Deserializer.class);

		methodInfo.setControllerMethod(consumeXml);

		when(request.getContentType()).thenReturn("application/xml; charset=UTF-8");
		when(deserializer.deserialize(null, consumeXml)).thenReturn(new Object[] {"abc", "def"});
		when(deserializers.deserializerFor("application/xml", container)).thenReturn(deserializer);

		observer.deserializes(new InterceptorsReady(consumeXml), request, methodInfo, status);

		assertEquals(methodInfo.getValuedParameters()[0].getValue(), "abc");
		assertEquals(methodInfo.getValuedParameters()[1].getValue(), "def");
	}

	@Test
	public void willDeserializeForAnyContentTypeIfPossible() throws Exception {
		final ControllerMethod consumesAnything = new DefaultControllerMethod(null, 
				DummyResource.class.getDeclaredMethod("consumesAnything", String.class, String.class));

		when(request.getContentType()).thenReturn("application/xml");

		methodInfo.setControllerMethod(consumesAnything);

		final Deserializer deserializer = mock(Deserializer.class);
		when(deserializer.deserialize(null, consumesAnything)).thenReturn(new Object[] {"abc", "def"});

		when(deserializers.deserializerFor("application/xml", container)).thenReturn(deserializer);
		observer.deserializes(new InterceptorsReady(consumesAnything), request, methodInfo, status);

		assertEquals(methodInfo.getValuedParameters()[0].getValue(), "abc");
		assertEquals(methodInfo.getValuedParameters()[1].getValue(), "def");
	}

	@Test
	public void shouldNotDeserializeIfHasNoContentType() throws Exception {
		final ControllerMethod consumesAnything = new DefaultControllerMethod(null, DummyResource.class.getDeclaredMethod("consumesAnything", String.class, String.class));

		when(request.getContentType()).thenReturn(null);
		methodInfo.setControllerMethod(consumesAnything);
		observer.deserializes(new InterceptorsReady(consumesAnything), request, methodInfo, status);

		assertEquals(methodInfo.getValuedParameters()[0].getValue(), null);
		assertEquals(methodInfo.getValuedParameters()[1].getValue(), null);
	}

	@Test
	public void willSetOnlyNonNullParameters() throws Exception {
		final Deserializer deserializer = mock(Deserializer.class);

		methodInfo.setControllerMethod(consumeXml);
		methodInfo.getValuedParameters()[0].setValue("original1");
		methodInfo.getValuedParameters()[1].setValue("original2");

		when(request.getContentType()).thenReturn("application/xml");
		when(deserializer.deserialize(null, consumeXml)).thenReturn(new Object[] {null, "deserialized"});

		when(deserializers.deserializerFor("application/xml", container)).thenReturn(deserializer);
		observer.deserializes(new InterceptorsReady(consumeXml), request, methodInfo, status);

		assertEquals(methodInfo.getValuedParameters()[0].getValue(), "original1");
		assertEquals(methodInfo.getValuedParameters()[1].getValue(), "deserialized");
	}
}
