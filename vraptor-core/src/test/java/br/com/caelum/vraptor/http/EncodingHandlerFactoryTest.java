package br.com.caelum.vraptor.http;

import static org.hamcrest.Matchers.instanceOf;
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
    public void shouldReturnAUTF8HandlerWhenThereIsNoEncodingInitParameter() throws Exception {
        when(configuration.getEncoding()).thenReturn(null);

        EncodingHandlerFactory handlerFactory = new EncodingHandlerFactory(configuration);
        assertThat(handlerFactory.getInstance(), is(instanceOf(UTF8EncodingHandler.class)));
    }
    @Test
    public void shouldReturnAWebXmlHandlerWhenThereIsAnEncodingInitParameter() throws Exception {
    	when(configuration.getEncoding()).thenReturn("UTF-8");

        EncodingHandlerFactory handlerFactory = new EncodingHandlerFactory(configuration);
        assertThat(handlerFactory.getInstance(), is(instanceOf(WebXmlEncodingHandler.class)));
    }

}
