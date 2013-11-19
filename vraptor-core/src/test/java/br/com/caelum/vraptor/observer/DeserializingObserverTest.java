package br.com.caelum.vraptor.observer;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.com.caelum.vraptor.Consumes;
import br.com.caelum.vraptor.controller.ControllerMethod;
import br.com.caelum.vraptor.controller.DefaultControllerMethod;
import br.com.caelum.vraptor.core.MethodInfo;
import br.com.caelum.vraptor.deserialization.Deserializer;
import br.com.caelum.vraptor.deserialization.Deserializers;
import br.com.caelum.vraptor.events.ReadyToExecuteMethod;
import br.com.caelum.vraptor.ioc.Container;
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

		methodInfo = new MethodInfo();

		observer = new DeserializingObserver(deserializers, container);
		consumeXml = new DefaultControllerMethod(null, DummyResource.class.getDeclaredMethod("consumeXml"));
		doesntConsume = new DefaultControllerMethod(null, DummyResource.class.getDeclaredMethod("doesntConsume"));
	}


	static class DummyResource {
		@Consumes("application/xml")
		public void consumeXml() {}

		@Consumes()
		public void consumesAnything() {}

		public void doesntConsume() {}
	}

	@Test
	public void shouldOnlyAcceptMethodsWithConsumesAnnotation() throws Exception {
		observer.deserializes(new ReadyToExecuteMethod(doesntConsume), request, methodInfo, status);
		verifyZeroInteractions(request);
	}

	@Test
	public void willSetHttpStatusCode415IfTheControllerMethodDoesNotSupportTheGivenMediaTypes() throws Exception {
		when(request.getContentType()).thenReturn("image/jpeg");

		observer.deserializes(new ReadyToExecuteMethod(consumeXml), request, methodInfo, status);

		verify(status).unsupportedMediaType("Request with media type [image/jpeg]. Expecting one of [application/xml].");
	}

	@Test
	public void willSetHttpStatusCode415IfThereIsNoDeserializerButIsAccepted() throws Exception {
		when(request.getContentType()).thenReturn("application/xml");
		when(deserializers.deserializerFor("application/xml", container)).thenReturn(null);

		observer.deserializes(new ReadyToExecuteMethod(consumeXml), request, methodInfo, status);

		verify(status).unsupportedMediaType("Unable to handle media type [application/xml]: no deserializer found.");
	}

	@Test
	public void willSetMethodParametersWithDeserializationAndContinueStackAfterDeserialization() throws Exception {
		when(request.getContentType()).thenReturn("application/xml");

		final Deserializer deserializer = mock(Deserializer.class);
		methodInfo.setParameters(new Object[2]);
		when(deserializer.deserialize(null, consumeXml)).thenReturn(new Object[] {"abc", "def"});

		when(deserializers.deserializerFor("application/xml", container)).thenReturn(deserializer);

		observer.deserializes(new ReadyToExecuteMethod(consumeXml), request, methodInfo, status);

		assertEquals(methodInfo.getParameters()[0], "abc");
		assertEquals(methodInfo.getParameters()[1], "def");
	}

	@Test
	public void willSetMethodParametersWithDeserializationEvenIfTheContentTypeHasCharsetDeclaration() throws Exception {
		when(request.getContentType()).thenReturn("application/xml; charset=UTF-8");

		final Deserializer deserializer = mock(Deserializer.class);
		methodInfo.setParameters(new Object[2]);
		when(deserializer.deserialize(null, consumeXml)).thenReturn(new Object[] {"abc", "def"});

		when(deserializers.deserializerFor("application/xml", container)).thenReturn(deserializer);

		observer.deserializes(new ReadyToExecuteMethod(consumeXml), request, methodInfo, status);

		assertEquals(methodInfo.getParameters()[0], "abc");
		assertEquals(methodInfo.getParameters()[1], "def");
	}

	@Test
	public void willDeserializeForAnyContentTypeIfPossible() throws Exception {
		when(request.getContentType()).thenReturn("application/xml");

		methodInfo.setParameters(new Object[2]);
		final ControllerMethod consumesAnything = new DefaultControllerMethod(null, DummyResource.class.getDeclaredMethod("consumesAnything"));

		final Deserializer deserializer = mock(Deserializer.class);
		when(deserializer.deserialize(null, consumesAnything)).thenReturn(new Object[] {"abc", "def"});

		when(deserializers.deserializerFor("application/xml", container)).thenReturn(deserializer);
		observer.deserializes(new ReadyToExecuteMethod(consumesAnything), request, methodInfo, status);

		assertEquals(methodInfo.getParameters()[0], "abc");
		assertEquals(methodInfo.getParameters()[1], "def");
	}

	@Test
	public void willSetOnlyNonNullParameters() throws Exception {
		when(request.getContentType()).thenReturn("application/xml");

		final Deserializer deserializer = mock(Deserializer.class);
		methodInfo.setParameters(new Object[] {"original1", "original2"});
		when(deserializer.deserialize(null, consumeXml)).thenReturn(new Object[] {null, "deserialized"});

		when(deserializers.deserializerFor("application/xml", container)).thenReturn(deserializer);
		observer.deserializes(new ReadyToExecuteMethod(consumeXml), request, methodInfo, status);

		assertEquals(methodInfo.getParameters()[0], "original1");
		assertEquals(methodInfo.getParameters()[1], "deserialized");
	}

	@Test(expected = IOException.class)
	public void shouldThrowInterceptionExceptionIfAnIOExceptionOccurs() throws Exception {
		when(request.getInputStream()).thenThrow(new IOException());
		when(request.getContentType()).thenReturn("application/xml");

		final Deserializer deserializer = mock(Deserializer.class);
		when(deserializers.deserializerFor("application/xml", container)).thenReturn(deserializer);
		observer.deserializes(new ReadyToExecuteMethod(consumeXml), request, methodInfo, status);
	}
}
