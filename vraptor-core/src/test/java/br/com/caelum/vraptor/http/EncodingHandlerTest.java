package br.com.caelum.vraptor.http;

import static br.com.caelum.vraptor.http.EncodingHandler.ENCODING_KEY;
import static java.nio.charset.Charset.defaultCharset;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.servlet.ServletContext;

import org.junit.Before;
import org.junit.Test;

public class EncodingHandlerTest {

	private ServletContext context;

	@Before
	public void setUp() throws Exception {
		context = mock(ServletContext.class);
	}

	@Test
	public void shouldReturnDefaultCharsetWhenThereIsNoEncodingInitParameter() throws Exception {
		when(context.getInitParameter(ENCODING_KEY)).thenReturn(null);

		EncodingHandler encodingHandler = new EncodingHandler(context);
		encodingHandler.init();
		
		assertThat(encodingHandler.getEncoding(), is(defaultCharset().name()));
	}
	
	@Test
	public void shouldReturnWebxmlValueWhenThereIsAnEncodingInitParameter() throws Exception {
		when(context.getInitParameter(ENCODING_KEY)).thenReturn("ISO-8859-1");
		
		EncodingHandler encodingHandler = new EncodingHandler(context);
		encodingHandler.init();
		
		assertThat(encodingHandler.getEncoding(), is("ISO-8859-1"));
	}
}
