package br.com.caelum.vraptor.deserialization;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.controller.DefaultBeanClass;
import br.com.caelum.vraptor.deserialization.Deserializer;
import br.com.caelum.vraptor.deserialization.Deserializers;
import br.com.caelum.vraptor.deserialization.DeserializesHandler;

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
