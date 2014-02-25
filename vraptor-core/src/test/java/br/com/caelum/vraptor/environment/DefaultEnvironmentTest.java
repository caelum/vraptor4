package br.com.caelum.vraptor.environment;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.URL;
import java.util.NoSuchElementException;

import javax.servlet.ServletContext;

import org.junit.Test;

public class DefaultEnvironmentTest {

	@Test
	public void shouldUseTheCurrentEnvironmentFileIfFound() throws IOException {
		ServletContext context = mock(ServletContext.class);
		ServletBasedEnvironment env = new ServletBasedEnvironment(context);
		URL resource = env.getResource("/hibernate.cfg.xml");
		assertThat(resource, is(equalTo(DefaultEnvironment.class.getResource("/development/hibernate.cfg.xml"))));
	}

	@Test
	public void shouldLoadConfigurationInDefaultFileEnvironment() throws IOException {
		ServletContext context = mock(ServletContext.class);
		when(context.getInitParameter("br.com.caelum.vraptor.environment")).thenReturn("production");
		ServletBasedEnvironment env = new ServletBasedEnvironment(context);

		assertThat(env.get("env_name"), is(equalTo("production")));
		assertEquals("only_in_default_file", env.get("only_in_default_file"));
	}

	@Test
	public void shouldUseTheDefaultFileIfEnvironmentIsNotFound() throws IOException {
		ServletContext context = mock(ServletContext.class);
		when(context.getInitParameter("br.com.caelum.vraptor.environment")).thenReturn("production");
		ServletBasedEnvironment env = new ServletBasedEnvironment(context);
		URL resource = env.getResource("/hibernate.cfg.xml");
		assertThat(resource, is(equalTo(DefaultEnvironment.class.getResource("/hibernate.cfg.xml"))));
		assertThat(env.get("env_name"), is(equalTo("production")));
	}

	@Test(expected = NoSuchElementException.class)
	public void shouldThrowExceptionIfKeyDoesNotExist() throws Exception {
		ServletContext context = mock(ServletContext.class);
		ServletBasedEnvironment env = new ServletBasedEnvironment(context);
		env.get("key_that_doesnt_exist");
	}

	@Test
	public void shouldGetDefaultValueIfTheValueIsntSet() throws Exception {
		DefaultEnvironment defaultEnvironment = new DefaultEnvironment(EnvironmentType.DEVELOPMENT);
		String value = defaultEnvironment.get("inexistent", "fallback");
		assertEquals("fallback", value);
	}

	@Test
	public void shouldGetValueIfIsSetInProperties() throws Exception {
		DefaultEnvironment defaultEnvironment = new DefaultEnvironment(EnvironmentType.DEVELOPMENT);
		String value = defaultEnvironment.get("env_name", "fallback");
		assertEquals("development", value);
	}
}