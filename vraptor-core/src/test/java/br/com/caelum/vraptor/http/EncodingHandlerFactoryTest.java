package br.com.caelum.vraptor.http;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.config.BasicConfiguration;

public class EncodingHandlerFactoryTest {

    private BasicConfiguration configuration;

    @Before
    public void setUp() throws Exception {
        configuration = mock(BasicConfiguration.class);
    }

    @Test
    public void shouldReturnUTF8WhenThereIsNoEncodingInitParameter() throws Exception {
        when(configuration.getEncoding()).thenReturn(null);

        EncodingHandlerFactory handlerFactory = new EncodingHandlerFactory(configuration);
        assertThat(handlerFactory.getInstance().getEncoding(), is("UTF-8"));
    }
    @Test
    public void shouldReturnWebxmlValueWhenThereIsAnEncodingInitParameter() throws Exception {
    	when(configuration.getEncoding()).thenReturn("ISO-8859-1");

        EncodingHandlerFactory handlerFactory = new EncodingHandlerFactory(configuration);
        assertThat(handlerFactory.getInstance().getEncoding(), is("ISO-8859-1"));
    }

}
