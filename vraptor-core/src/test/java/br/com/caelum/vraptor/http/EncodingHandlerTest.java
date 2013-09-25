package br.com.caelum.vraptor.http;

import static java.nio.charset.Charset.defaultCharset;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.servlet.ServletContext;

import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.config.BasicConfiguration;

public class EncodingHandlerTest {

	private BasicConfiguration configuration;
	private ServletContext context;

	@Before
	public void setUp() throws Exception {
		configuration = mock(BasicConfiguration.class);
		context = mock(ServletContext.class);
	}

	@Test
	public void shouldReturnUTF8WhenThereIsNoEncodingInitParameter() throws Exception {
		when(configuration.getEncoding()).thenReturn(null);

		EncodingHandler encodingHandler = new EncodingHandler(configuration);
		assertThat(encodingHandler.getEncoding(), is(defaultCharset().name()));
	}
	@Test
	public void shouldReturnWebxmlValueWhenThereIsAnEncodingInitParameter() throws Exception {
		configuration = new BasicConfiguration(context);
		when(context.getInitParameter(anyString())).thenReturn("ISO-8859-1");

		EncodingHandler encodingHandler = new EncodingHandler(configuration);
		assertThat(encodingHandler.getEncoding(), is("ISO-8859-1"));
	}

}
