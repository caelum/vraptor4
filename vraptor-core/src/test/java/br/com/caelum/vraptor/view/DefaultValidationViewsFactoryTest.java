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
package br.com.caelum.vraptor.view;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.controller.HttpMethod;
import br.com.caelum.vraptor.core.DefaultReflectionProvider;
import br.com.caelum.vraptor.proxy.JavassistProxifier;
import br.com.caelum.vraptor.proxy.Proxifier;
import br.com.caelum.vraptor.serialization.JSONSerialization;
import br.com.caelum.vraptor.serialization.SerializerBuilder;
import br.com.caelum.vraptor.util.test.MockedLogic;
import br.com.caelum.vraptor.validator.Message;
import br.com.caelum.vraptor.validator.ValidationException;

public class DefaultValidationViewsFactoryTest {

	@Rule
	public ExpectedException exception = ExpectedException.none();

	private Result result;
	private Proxifier proxifier;
	private DefaultValidationViewsFactory factory;
	private List<Message> errors;
	private SerializerBuilder serializerBuilder;

	@Before
	public void setUp() throws Exception {
		result = mock(Result.class);

		proxifier = new JavassistProxifier();
		factory = new DefaultValidationViewsFactory(result, proxifier, new DefaultReflectionProvider());
		errors = Collections.emptyList();

	}


	public static class RandomComponent {
		public void random() {

		}
	}

	@Test
	public void shouldUseValidationVersionOfLogicResult() throws Exception {
		exception.expect(ValidationException.class);

		when(result.use(LogicResult.class)).thenReturn(new MockedLogic());
		factory.instanceFor(LogicResult.class, errors).forwardTo(RandomComponent.class).random();
	}

	@Test
	public void shouldThrowExceptionOnlyAtTheEndOfValidationCall() throws Exception {

		when(result.use(LogicResult.class)).thenReturn(new MockedLogic());
		when(result.use(PageResult.class)).thenReturn(new MockedPage());

		factory.instanceFor(LogicResult.class, errors);
		factory.instanceFor(LogicResult.class, errors).forwardTo(RandomComponent.class);
		factory.instanceFor(PageResult.class, errors);
		factory.instanceFor(PageResult.class, errors).of(RandomComponent.class);
	}

	@Test
	public void shouldUseValidationVersionOfPageResult() throws Exception {
		exception.expect(ValidationException.class);

		when(result.use(PageResult.class)).thenReturn(new MockedPage());
		factory.instanceFor(PageResult.class, errors).forwardTo("any uri");
	}

	@Test
	public void shouldUseValidationVersionOfEmptyResult() throws Exception {
		exception.expect(ValidationException.class);

		when(result.use(EmptyResult.class)).thenReturn(new EmptyResult());
		factory.instanceFor(EmptyResult.class, errors);
	}

	@Test
	public void onHttpResultShouldNotThrowExceptionsOnHeaders() throws Exception {
		HttpResult httpResult = mock(HttpResult.class);

		when(result.use(HttpResult.class)).thenReturn(httpResult);

		factory.instanceFor(HttpResult.class, errors);
		factory.instanceFor(HttpResult.class, errors).addDateHeader("abc", 123l);
		factory.instanceFor(HttpResult.class, errors).addHeader("def", "ghi");
		factory.instanceFor(HttpResult.class, errors).addIntHeader("jkl", 456);
		factory.instanceFor(HttpResult.class, errors).addIntHeader("jkl", 456);
	}

	@Test
	public void onHttpResultShouldThrowExceptionsOnSendError() throws Exception {
		exception.expect(ValidationException.class);

		HttpResult httpResult = mock(HttpResult.class);
		when(result.use(HttpResult.class)).thenReturn(httpResult);

		factory.instanceFor(HttpResult.class, errors).sendError(404);
	}

	@Test
	public void onHttpResultShouldThrowExceptionsOnSendErrorWithMessage() throws Exception {
		exception.expect(ValidationException.class);

		HttpResult httpResult = mock(HttpResult.class);
		when(result.use(HttpResult.class)).thenReturn(httpResult);

		factory.instanceFor(HttpResult.class, errors).sendError(404, "Not Found");
	}

	@Test
	public void onHttpResultShouldThrowExceptionsOnSetStatus() throws Exception {
		exception.expect(ValidationException.class);

		HttpResult httpResult = mock(HttpResult.class);
		when(result.use(HttpResult.class)).thenReturn(httpResult);

		factory.instanceFor(HttpResult.class, errors).setStatusCode(200);
	}

	@Test
	public void shouldBeAbleToChainMethodsOnHttpResult() throws Exception {
		HttpResult httpResult = mock(HttpResult.class);

		when(result.use(HttpResult.class)).thenReturn(httpResult);

		factory.instanceFor(HttpResult.class, errors).addDateHeader("abc", 123l).addHeader("def", "ghi").addIntHeader("jkl", 234);
	}

	@Test
	public void onStatusResultShouldThrowExceptionsOnMoved() throws Exception {
		exception.expect(ValidationException.class);

		Status status = mock(Status.class);
		when(result.use(Status.class)).thenReturn(status);
		when(status.movedPermanentlyTo(RandomComponent.class)).thenReturn(new RandomComponent());

		try {
			factory.instanceFor(Status.class, errors).movedPermanentlyTo(RandomComponent.class);
		} catch (ValidationException e) {
			Assert.fail("The exception must occur only on method call");
		}
		factory.instanceFor(Status.class, errors).movedPermanentlyTo(RandomComponent.class).random();
	}

	@Test
	public void onStatusResultShouldThrowExceptionsOnMovedToLogic() throws Exception {
		exception.expect(ValidationException.class);

		Status status = mock(Status.class);
		when(result.use(Status.class)).thenReturn(status);

		factory.instanceFor(Status.class, errors).movedPermanentlyTo("anywhere");
	}

	@Test
	public void onRefererResultShouldThrowExceptionsOnForward() throws Exception {
		exception.expect(ValidationException.class);

		RefererResult referer = mock(RefererResult.class);
		when(result.use(RefererResult.class)).thenReturn(referer);

		factory.instanceFor(RefererResult.class, errors).forward();
	}

	@Test
	public void onRefererResultShouldThrowExceptionsOnRedirect() throws Exception {
		exception.expect(ValidationException.class);

		RefererResult referer = mock(RefererResult.class);
		when(result.use(RefererResult.class)).thenReturn(referer);

		factory.instanceFor(RefererResult.class, errors).redirect();
	}

	@Test
	public void onStatusResultShouldThrowExceptionsOnNotFound() throws Exception {
		exception.expect(ValidationException.class);

		Status status = mock(Status.class);
		when(result.use(Status.class)).thenReturn(status);

		factory.instanceFor(Status.class, errors).notFound();

	}

	@Test
	public void onStatusResultShouldThrowExceptionsOnHeader() throws Exception {
		exception.expect(ValidationException.class);

		Status status = mock(Status.class);
		when(result.use(Status.class)).thenReturn(status);

		factory.instanceFor(Status.class, errors).header("abc", "def");
	}

	@Test
	public void onStatusResultShouldThrowExceptionsOnCreated() throws Exception {
		exception.expect(ValidationException.class);

		Status status = mock(Status.class);
		when(result.use(Status.class)).thenReturn(status);

		factory.instanceFor(Status.class, errors).created();
	}

	@Test
	public void onStatusResultShouldThrowExceptionsOnCreatedWithLocation() throws Exception {
		exception.expect(ValidationException.class);

		Status status = mock(Status.class);
		when(result.use(Status.class)).thenReturn(status);

		factory.instanceFor(Status.class, errors).created("/newLocation");
	}

	@Test
	public void onStatusResultShouldThrowExceptionsOnOk() throws Exception {
		exception.expect(ValidationException.class);

		Status status = mock(Status.class);
		when(result.use(Status.class)).thenReturn(status);

		factory.instanceFor(Status.class, errors).ok();
	}

	@Test
	public void onStatusResultShouldThrowExceptionsOnConflict() throws Exception {
		exception.expect(ValidationException.class);

		Status status = mock(Status.class);
		when(result.use(Status.class)).thenReturn(status);

		factory.instanceFor(Status.class, errors).conflict();
	}

	@Test
	public void onStatusResultShouldThrowExceptionsOnMethodNotAllowed() throws Exception {
		exception.expect(ValidationException.class);

		Status status = mock(Status.class);
		when(result.use(Status.class)).thenReturn(status);

		factory.instanceFor(Status.class, errors).methodNotAllowed(EnumSet.allOf(HttpMethod.class));
	}

	@Test
	public void onStatusResultShouldThrowExceptionsOnMovedPermanentlyTo() throws Exception {
		exception.expect(ValidationException.class);

		Status status = mock(Status.class);
		when(result.use(Status.class)).thenReturn(status);

		factory.instanceFor(Status.class, errors).movedPermanentlyTo("/newUri");
	}

	@Test
	public void onStatusResultShouldThrowExceptionsOnMovedPermanentlyToLogic() throws Exception {
		exception.expect(ValidationException.class);

		Status status = mock(Status.class);

		when(result.use(Status.class)).thenReturn(status);
		when(status.movedPermanentlyTo(RandomComponent.class)).thenReturn(new RandomComponent());

		try {
			factory.instanceFor(Status.class, errors).movedPermanentlyTo(RandomComponent.class);
		} catch (ValidationException e) {
			Assert.fail("Should not throw exception yet");
		}
		factory.instanceFor(Status.class, errors).movedPermanentlyTo(RandomComponent.class).random();
	}

	@Test
	public void onXMLSerializationResultShouldThrowExceptionOnlyOnSerializeMethod() throws Exception {
		exception.expect(ValidationException.class);

		JSONSerialization serialization = mock(JSONSerialization.class);

		serializerBuilder = mock(SerializerBuilder.class, new Answer<SerializerBuilder>() {
			@Override
			public SerializerBuilder answer(InvocationOnMock invocation) throws Throwable {
				return serializerBuilder;
			}
		});

		when(result.use(JSONSerialization.class)).thenReturn(serialization);
		when(serialization.from(any())).thenReturn(serializerBuilder);

		try {
			factory.instanceFor(JSONSerialization.class, errors).from(new Object());
			factory.instanceFor(JSONSerialization.class, errors).from(new Object()).include("abc");
			factory.instanceFor(JSONSerialization.class, errors).from(new Object()).exclude("abc");
		} catch (ValidationException e) {
			Assert.fail("Should not throw exception yet");
		}
		factory.instanceFor(JSONSerialization.class, errors).from(new Object()).serialize();
	}

	static class RandomSerializer implements SerializerBuilder {

		@Override
		public RandomSerializer exclude(String... names) {
			return this;
		}

		@Override
		public RandomSerializer excludeAll() {
			return this;
		}

		@Override
		public <T> RandomSerializer from(T object) {
			return this;
		}

		@Override
		public <T> RandomSerializer from(T object, String alias) {
			return this;
		}

		@Override
		public RandomSerializer include(String... names) {
			return this;
		}

		@Override
		public RandomSerializer recursive() {
			return this;
		}

		@Override
		public void serialize() {
		}
	}

	@Test
	public void onSerializerResultsShouldBeAbleToCreateValidationInstancesEvenIfChildClassesUsesCovariantType() throws Exception {
		exception.expect(ValidationException.class);

		JSONSerialization serialization = mock(JSONSerialization.class);

		serializerBuilder = new RandomSerializer();

		when(result.use(JSONSerialization.class)).thenReturn(serialization);
		when(serialization.from(any())).thenReturn(serializerBuilder);

		try {
			factory.instanceFor(JSONSerialization.class, errors).from(new Object());
			factory.instanceFor(JSONSerialization.class, errors).from(new Object()).include("abc");
			factory.instanceFor(JSONSerialization.class, errors).from(new Object()).exclude("abc");
		} catch (ValidationException e) {
			Assert.fail("Should not throw exception yet");
		}
		factory.instanceFor(JSONSerialization.class, errors).from(new Object()).serialize();
	}
}
