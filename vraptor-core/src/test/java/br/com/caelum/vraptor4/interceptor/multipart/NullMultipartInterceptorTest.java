package br.com.caelum.vraptor4.interceptor.multipart;

import static org.junit.Assert.assertFalse;

import org.junit.Test;

public class NullMultipartInterceptorTest {

	@Test
	public void shouldNeverAccept() {
		assertFalse(new NullMultipartInterceptor().accepts(null));
	}
	
	@Test(expected=UnsupportedOperationException.class)
	public void shouldThrowUnsuportedOperationExceptionWhenInvoked() {
		new NullMultipartInterceptor().intercept(null, null, null);
	}
}
