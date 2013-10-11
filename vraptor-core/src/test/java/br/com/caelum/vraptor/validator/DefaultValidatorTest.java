/***
 * Copyright (c) 2009 Caelum - www.caelum.com.br/opensource
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package br.com.caelum.vraptor.validator;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.ResourceBundle;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import br.com.caelum.vraptor.Controller;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.core.SafeResourceBundle;
import br.com.caelum.vraptor.proxy.JavassistProxifier;
import br.com.caelum.vraptor.proxy.Proxifier;
import br.com.caelum.vraptor.util.test.MockResult;
import br.com.caelum.vraptor.view.DefaultValidationViewsFactory;
import br.com.caelum.vraptor.view.LogicResult;
import br.com.caelum.vraptor.view.PageResult;

@RunWith(MockitoJUnitRunner.class)
public class DefaultValidatorTest {

	private static final Message A_MESSAGE = new SimpleMessage("", "");
	private @Mock Result result = new MockResult();
	private @Mock LogicResult logicResult;
	private @Mock PageResult pageResult;
	private @Mock Outjector outjector;

	private @Mock MyComponent instance;
	private DefaultValidator validator;

	@Before
	public void setup() {
		ResourceBundle bundle = new SafeResourceBundle(ResourceBundle.getBundle("messages"));

		Proxifier proxifier = new JavassistProxifier();
		this.validator = new DefaultValidator(result, new DefaultValidationViewsFactory(result, proxifier), outjector, proxifier, bundle);
		when(result.use(LogicResult.class)).thenReturn(logicResult);
		when(result.use(PageResult.class)).thenReturn(pageResult);
		when(logicResult.forwardTo(MyComponent.class)).thenReturn(instance);
		when(pageResult.of(MyComponent.class)).thenReturn(instance);
	}

	@Test
	public void outjectsTheRequestParameters() {
		try {
			validator.add(A_MESSAGE);
			validator.onErrorForwardTo(MyComponent.class).logic();
		} catch (ValidationException e) {
		}
		verify(outjector).outjectRequestMap();
	}

	@Test
	public void addsTheErrorsOnTheResult() {
		try {
			validator.add(A_MESSAGE);
			validator.onErrorForwardTo(MyComponent.class).logic();
		} catch (ValidationException e) {
		}
		verify(result).include(eq("errors"), argThat(is(not(empty()))));
	}

	@Test
	public void forwardToCustomOnErrorPage() {
		try {
			when(logicResult.forwardTo(MyComponent.class)).thenReturn(instance);
			validator.add(A_MESSAGE);
			validator.onErrorForwardTo(MyComponent.class).logic();
			fail("should stop flow");
		} catch (ValidationException e) {
			verify(instance).logic();
		}
	}

	@Test
	public void shouldNotRedirectIfHasNotErrors() {
		try {
			validator.onErrorRedirectTo(MyComponent.class).logic();
			assertThat(validator.getErrors(), hasSize(0));
			verify(outjector, never()).outjectRequestMap();
		} catch (ValidationException e) {
			fail("no error occurs");
		}
	}

	@Test
	public void testThatValidatorGoToRedirectsToTheErrorPageImmediatellyAndNotBeforeThis() {
		try {
			// call all other validation methods and don't expect them to redirect
			validator.addAll(Arrays.asList(new SimpleMessage("test", "test")));

			when(pageResult.of(MyComponent.class)).thenReturn(instance);

			validator.onErrorUsePageOf(MyComponent.class).logic();
			fail("should stop flow");
		} catch (ValidationException e) {
			verify(instance).logic();
		}
	}

	@Test
	public void shouldParametrizeMessage() {
		Message message0 = new SimpleMessage("category", "The simple message");
		Message message1 = new SimpleMessage("category", "The {0} message", "simple");

		assertThat(message0.getMessage(), is("The simple message"));
		assertThat(message1.getMessage(), is("The simple message"));
	}

	@Test
	public void doNothingIfCheckingSuccess() {
		Client c = new Client();
		c.name = "The name";

		validator.check(c.name != null, new SimpleMessage("client.name", "not null"));
		assertThat(validator.getErrors(), hasSize(0));
	}

	@Test
	public void shouldAddMessageIfCheckingFails() {
		Client c = new Client();

		validator.check(c.name != null, new SimpleMessage("client.name", "not null"));
		assertThat(validator.getErrors(), hasSize(1));
		assertThat(validator.getErrors().get(0).getMessage(), containsString("not null"));
	}

	@Controller
	public static interface MyComponent {
		public void logic();
	}

	static class Client {
		public String name;
		public int age;
	}

}
