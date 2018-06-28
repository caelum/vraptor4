package br.com.caelum.vraptor.observer;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import br.com.caelum.vraptor.controller.DefaultControllerMethod;
import br.com.caelum.vraptor.core.MethodInfo;
import br.com.caelum.vraptor.events.InterceptorsReady;
import br.com.caelum.vraptor.http.MutableRequest;
import br.com.caelum.vraptor.http.ParametersProvider;
import br.com.caelum.vraptor.http.ParanamerNameProvider;
import br.com.caelum.vraptor.ioc.Container;
import br.com.caelum.vraptor.serialization.Deserializer;
import br.com.caelum.vraptor.serialization.Deserializers;
import br.com.caelum.vraptor.validator.Message;
import br.com.caelum.vraptor.validator.Validator;
import br.com.caelum.vraptor.view.FlashScope;
import br.com.caelum.vraptor.view.Status;

@RunWith(MockitoJUnitRunner.class)
public class DeserializingAndInstantiatorIntegrationTest {

	@Mock private ParametersProvider provider;
	@Mock private Validator validator;
	@Mock private MutableRequest request;
	@Mock private FlashScope flash;
	@Mock private Deserializers deserializers;
	@Mock private Container container;
	@Mock private Status status;
	@Mock private Deserializer deserializer;
	
	private MethodInfo methodInfo;
	private ParametersInstantiator instantiator;
	private DeserializingObserver deserializing;
	
	private DefaultControllerMethod controllerMethod;
	

	@Before
	public void setUp() throws Exception {
		when(request.getParameterNames()).thenReturn(Collections.<String> emptyEnumeration());
		when(deserializers.deserializerFor("application/xml", container)).thenReturn(deserializer);
		when(request.getContentType()).thenReturn("application/xml");
		
		methodInfo = new MethodInfo(new ParanamerNameProvider());
		instantiator = new ParametersInstantiator(provider, methodInfo, validator, request, flash);
		deserializing = new DeserializingObserver(deserializers, container);
		
		controllerMethod = new DefaultControllerMethod(null, DeserializingObserverTest
				.DummyResource.class.getDeclaredMethod("consumeXml", String.class, String.class));
		
		when(provider.getParametersFor(controllerMethod, Collections.<Message>emptyList())).thenReturn(new Object[] { "123", "ignored"});
		
		when(deserializer.deserialize(null, controllerMethod)).thenReturn(new Object[] {null, "XMlValue"});
		methodInfo.setControllerMethod(controllerMethod);
	}
	
	@Test
	public void shouldDeserializeWhenInstantiatorRunsBefore() throws Exception {
		instantiator.instantiate(new InterceptorsReady(controllerMethod));
		deserializing.deserializes(new InterceptorsReady(controllerMethod), request, methodInfo, status);
		assertEquals("123", methodInfo.getValuedParameters()[0].getValue());
		assertEquals("XMlValue", methodInfo.getValuedParameters()[1].getValue());
	}	

	@Test
	public void shouldDeserializeWhenInstantiatorRunsAfter() throws Exception {
		deserializing.deserializes(new InterceptorsReady(controllerMethod), request, methodInfo, status);
		instantiator.instantiate(new InterceptorsReady(controllerMethod));
		assertEquals("123", methodInfo.getValuedParameters()[0].getValue());
		assertEquals("XMlValue", methodInfo.getValuedParameters()[1].getValue());
	}	
	
}
