/***
 * Copyright (c) 2009 Caelum - www.caelum.com.br/opensource All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package br.com.caelum.vraptor.environment;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.URL;
import java.util.NoSuchElementException;

import javax.servlet.ServletContext;

import org.junit.Test;

public class ServletBasedEnvironmentTest {

	@Test
	public void shouldUseTheCurrentEnvironmentFileIfFound() throws IOException {
		ServletContext context = mock(ServletContext.class);
		ServletBasedEnvironment env = new ServletBasedEnvironment(context);
		URL resource = env.getResource("/hibernate.cfg.xml");
		assertThat(resource, is(equalTo(DefaultEnvironment.class.getResource("/development/hibernate.cfg.xml"))));
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

	@Test
	public void shouldNotUseAnyPropertiesIfItDoesntExist() throws IOException {
		ServletContext context = mock(ServletContext.class);
		when(context.getInitParameter("br.com.caelum.vraptor.environment")).thenReturn("test");
		ServletBasedEnvironment env = new ServletBasedEnvironment(context);
		URL resource = env.getResource("/hibernate.cfg.xml");
		assertThat(resource, is(equalTo(DefaultEnvironment.class.getResource("/hibernate.cfg.xml"))));
		assertFalse(env.has("unexistant_key"));
	}

	@Test(expected = NoSuchElementException.class)
	public void shouldThrowExceptionIfKeyDoesNotExist() throws Exception {
		ServletContext context = mock(ServletContext.class);
		ServletBasedEnvironment env = new ServletBasedEnvironment(context);
		env.get("key_that_doesnt_exist");
	}
}