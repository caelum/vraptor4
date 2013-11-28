package br.com.caelum.vraptor.config;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class ApplicationConfigurationTest {

	@Mock private HttpServletRequest request;
	private ApplicationConfiguration configuration;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		this.configuration = new ApplicationConfiguration(request);
		when(request.getScheme()).thenReturn("http");
		when(request.getServerName()).thenReturn("caelum.com.br");
		when(request.getContextPath()).thenReturn("/context/");
	}

	@Test
	public void shouldNotUsePortWhenPortIs80() {
		when(request.getServerPort()).thenReturn(80);
		assertEquals("http://caelum.com.br/context/", applicationPath());
	}

	@Test
	public void shouldGiveFullUrlWithPortWhenPortIsNot80() {
		when(request.getServerPort()).thenReturn(8080);
		assertEquals("http://caelum.com.br:8080/context/", applicationPath());
	}

	private String applicationPath() {
		return configuration.getApplicationPath();
	}

}