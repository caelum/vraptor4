package br.com.caelum.vraptor.ioc;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.inject.Inject;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import br.com.caelum.vraptor.MockStaticContentHandler;
import br.com.caelum.vraptor.VRaptor;
import br.com.caelum.vraptor.WeldJunitRunner;
import br.com.caelum.vraptor.ioc.cdi.Contexts;

@RunWith(WeldJunitRunner.class)
public class RequestStartedFactoryTest {

	@Inject private MockStaticContentHandler handler;
	@Inject private VRaptor vRaptor;
	@Inject private MockRequestHandlerObserver requestHandler;
	@Inject private Contexts contexts;

	private HttpServletRequest request;
	private HttpServletResponse response;
	private FilterChain chain;

	@Before
	public void setup() {
		request = mock(HttpServletRequest.class);
		response = mock(HttpServletResponse.class);
		chain = mock(FilterChain.class);
		handler.setRequestingStaticFile(false);
		contexts.startRequestScope();
	}

	@Test
	public void shouldNotRunVRaptorStackIfVRaptorRequestStartedEventNotFired() throws Exception {
		when(request.getRequestURI()).thenReturn(MockRequestStartedFactory.PATTERN_TO_AVOID_VRAPTOR_STACK);

		vRaptor.doFilter(request, response, chain);

		assertThat(requestHandler.isVraptorStackCalled(), is(false));
	}

	@Test
	public void shouldRunVRaptorStackIfVRaptorRequestStartedEventIsFired() throws Exception {
		when(request.getRequestURI()).thenReturn("someUrlThatMustBeInterceptedByVRaptor");

		vRaptor.doFilter(request, response, chain);

		assertThat(requestHandler.isVraptorStackCalled(), is(true));
	}

}
