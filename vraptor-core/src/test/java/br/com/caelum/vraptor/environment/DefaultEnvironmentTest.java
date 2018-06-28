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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.URL;
import java.util.NoSuchElementException;

import javax.servlet.ServletContext;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;

public class DefaultEnvironmentTest {

	@Rule
	public ExpectedException exception = ExpectedException.none();

	private ServletContext context;

	@Before
	public void setup() {
		context = Mockito.mock(ServletContext.class);
	}

	@Test
	public void shouldUseEnvironmentBasedFileIfFoundUnderEnvironmentFolder() throws IOException {
		DefaultEnvironment env = buildEnvironment(EnvironmentType.DEVELOPMENT);
		URL resource = env.getResource("/rules.txt");

		assertThat(resource, notNullValue());
		assertThat(resource, is(getClass().getResource("/development/rules.txt")));
	}

	@Test
	public void shouldUseRootBasedFileIfNotFoundUnderEnvironmentFolder() throws IOException {
		DefaultEnvironment env = buildEnvironment(EnvironmentType.PRODUCTION);
		URL resource = env.getResource("/rules.txt");

		assertThat(resource, notNullValue());
		assertThat(resource, is(getClass().getResource("/rules.txt")));
	}

	@Test
	public void shouldLoadConfigurationInDefaultFileEnvironment() throws IOException {
		when(context.getInitParameter(DefaultEnvironment.ENVIRONMENT_PROPERTY)).thenReturn("production");
		DefaultEnvironment env = buildEnvironment(context);
		
		assertThat(env.get("env_name"), is("production"));
		assertThat(env.get("only_in_default_file"), is("only_in_default_file"));
	}

	@Test
	public void shouldUseFalseWhenFeatureIsNotPresent() throws IOException {
		DefaultEnvironment env = buildEnvironment(context);
		assertThat(env.supports("feature_that_doesnt_exists"), is(false));
	}

	@Test
	public void shouldTrimValueWhenEvaluatingSupports() throws Exception {
		DefaultEnvironment env = buildEnvironment(context);
		assertThat(env.supports("untrimmed_boolean"), is(true));
	}

	@Test
	public void shouldThrowExceptionIfKeyDoesNotExist() throws Exception {
		exception.expect(NoSuchElementException.class);

		DefaultEnvironment env = buildEnvironment(context);
		env.get("key_that_doesnt_exist");
	}

	@Test
	public void shouldGetValueWhenIsPresent() throws Exception {
		DefaultEnvironment env = buildEnvironment(context);
		String value = env.get("env_name", "fallback");
		assertThat(value, is("development"));
	}

	@Test
	public void shouldGetDefaultValueWhenIsntPresent() throws Exception {
		DefaultEnvironment env = buildEnvironment(context);
		String value = env.get("inexistent", "fallback");
		assertThat(value, is("fallback"));
	}

	@Test
	public void shouldAllowApplicationToOverrideProperties() throws Exception {
		DefaultEnvironment env = buildEnvironment(context);
		env.setup();

		assertThat(env.get("itWorks"), is("It Works!"));

		env.set("itWorks", "Yep, works fine.");
		assertThat(env.get("itWorks"), is("Yep, works fine."));
	}

	@Test
	public void shouldUseContextInitParameterWhenSystemPropertiesIsntPresent() {
		when(context.getInitParameter(DefaultEnvironment.ENVIRONMENT_PROPERTY)).thenReturn("acceptance");
		DefaultEnvironment env = buildEnvironment(context);
		
		assertThat(env.getName(), is("acceptance"));
	}

	@Test
	public void shouldUseSystemPropertiesWhenSysenvIsntPresent() {
		System.getProperties().setProperty(DefaultEnvironment.ENVIRONMENT_PROPERTY, "acceptance");
		DefaultEnvironment env = buildEnvironment(context);

		verify(context, never()).getInitParameter(DefaultEnvironment.ENVIRONMENT_PROPERTY);

		assertThat(env.getName(), is("acceptance"));
		System.getProperties().remove(DefaultEnvironment.ENVIRONMENT_PROPERTY);
	}
	
	public void shouldGetOverridedSystemPropertyValueIfIsSet() throws IOException {
		DefaultEnvironment defaultEnvironment = buildEnvironment(EnvironmentType.DEVELOPMENT);
		System.setProperty("env_name", "customEnv");
		String value = defaultEnvironment.get("env_name");
		assertThat("customEnv", is(value));
		//unset property to not break other tests
		System.setProperty("env_name", "");
	}
	
	private DefaultEnvironment buildEnvironment(EnvironmentType environmentType) {
		DefaultEnvironment defaultEnvironment = new DefaultEnvironment(environmentType);
		
		return defaultEnvironment;
	}
	
	private DefaultEnvironment buildEnvironment(ServletContext context) {
		DefaultEnvironment defaultEnvironment = new DefaultEnvironment(context);
		defaultEnvironment.setup();
		
		return defaultEnvironment;
	}
	
}
