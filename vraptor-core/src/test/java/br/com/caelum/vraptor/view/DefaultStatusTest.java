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
package br.com.caelum.vraptor.view;

import static br.com.caelum.vraptor.serialization.xstream.XStreamBuilderImpl.cleanInstance;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.View;
import br.com.caelum.vraptor.config.Configuration;
import br.com.caelum.vraptor.controller.HttpMethod;
import br.com.caelum.vraptor.core.DefaultReflectionProvider;
import br.com.caelum.vraptor.http.FormatResolver;
import br.com.caelum.vraptor.http.route.Router;
import br.com.caelum.vraptor.proxy.JavassistProxifier;
import br.com.caelum.vraptor.serialization.DefaultRepresentationResult;
import br.com.caelum.vraptor.serialization.JSONSerialization;
import br.com.caelum.vraptor.serialization.Serialization;
import br.com.caelum.vraptor.serialization.Serializee;
import br.com.caelum.vraptor.serialization.gson.GsonBuilderWrapper;
import br.com.caelum.vraptor.serialization.gson.GsonSerializerBuilder;
import br.com.caelum.vraptor.serialization.gson.MessageGsonConverter;
import br.com.caelum.vraptor.serialization.xstream.MessageConverter;
import br.com.caelum.vraptor.serialization.xstream.XStreamBuilder;
import br.com.caelum.vraptor.util.test.MockInstanceImpl;
import br.com.caelum.vraptor.util.test.MockSerializationResult;
import br.com.caelum.vraptor.validator.I18nMessage;
import br.com.caelum.vraptor.validator.Message;
import br.com.caelum.vraptor.validator.SimpleMessage;
import br.com.caelum.vraptor.validator.SingletonResourceBundle;

import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSerializer;

public class DefaultStatusTest {

	private @Mock HttpServletResponse response;
	private @Mock Result result;
	private @Mock Configuration config;
	private @Mock Router router;

	private Status status;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		status = new DefaultStatus(response, result, config, new JavassistProxifier(), router);
	}

	@Test
	public void shouldSetNotFoundStatus() throws Exception {
		status.notFound();

		verify(response).sendError(404);
	}

	@Test
	public void shouldSetHeader() throws Exception {
		status.header("Content-type", "application/xml");

		verify(response).addHeader("Content-type", "application/xml");
	}

	@Test
	public void shouldSetCreatedStatus() throws Exception {
		status.created();

		verify(response).setStatus(201);
	}

	@Test
	public void shouldSetCreatedStatusAndLocationWithAppPath() throws Exception {
		when(config.getApplicationPath()).thenReturn("http://myapp.com");
		status.created("/newResource");

		verify(response).setStatus(201);
		verify(response).addHeader("Location", "http://myapp.com/newResource");
	}

	@Test
	public void shouldSetOkStatus() throws Exception {
		status.ok();

		verify(response).setStatus(200);
	}

	@Test
	public void shouldSetConflictStatus() throws Exception {
		status.conflict();

		verify(response).sendError(409);
	}

	@Test
	public void shouldSetAcceptedStatus() throws Exception {
		status.accepted();

		verify(response).setStatus(202);
	}

	@Test
	public void shouldSetNotImplementedStatus() throws Exception {
		status.notImplemented();

		verify(response).setStatus(501);
	}
	
	@Test
	public void shouldSetInternalServerErrorStatus() throws Exception {
		status.internalServerError();

		verify(response).setStatus(500);
	}

	@Test
	public void shouldSetMethodNotAllowedStatus() throws Exception {
		status.methodNotAllowed(EnumSet.of(HttpMethod.GET, HttpMethod.POST));

		verify(response).sendError(405);
		verify(response).addHeader("Allow", "GET, POST");
	}

	@Test
	public void shouldSetMovedPermanentlyStatus() throws Exception {
		when(config.getApplicationPath()).thenReturn("http://myapp.com");

		status.movedPermanentlyTo("/newURL");

		verify(response).setStatus(301);
		verify(response).addHeader("Location", "http://myapp.com/newURL");
	}
	@Test
	public void shouldMoveToExactlyURIWhenItIsNotAbsolute() throws Exception {

		status.movedPermanentlyTo("http://www.caelum.com.br");

		verify(response).addHeader("Location", "http://www.caelum.com.br");
		verify(response).setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
	}

	static interface Resource {
		void method();
	}

	@Test
	public void shouldSetMovedPermanentlyStatusOfLogic() throws Exception {
		when(config.getApplicationPath()).thenReturn("http://myapp.com");
		Method method = Resource.class.getDeclaredMethod("method");
		when(router.urlFor(eq(Resource.class), eq(method), Mockito.anyVararg())).thenReturn("/resource/method");

		status.movedPermanentlyTo(Resource.class).method();

		verify(response).setStatus(301);
		verify(response).addHeader("Location", "http://myapp.com/resource/method");
	}

	@SuppressWarnings("deprecation")
	@Test
	public void shouldSerializeErrorMessages() throws Exception {
		Message normal = new SimpleMessage("category", "The message");
		I18nMessage i18ned = new I18nMessage("category", "message");
		i18ned.setBundle(new SingletonResourceBundle("message", "Something else"));

		XStreamBuilder xstreamBuilder = cleanInstance(new MessageConverter());
		MockSerializationResult result = new MockSerializationResult(null, xstreamBuilder, null, null);
		DefaultStatus status = new DefaultStatus(response, result, config, new JavassistProxifier(), router);

		status.badRequest(Arrays.asList(normal, i18ned));

		String serialized = result.serializedResult();
		assertThat(serialized, containsString("<message>The message</message>"));
		assertThat(serialized, containsString("<category>category</category>"));
		assertThat(serialized, containsString("<message>Something else</message>"));
		assertThat(serialized, not(containsString("<validationMessage>")));
		assertThat(serialized, not(containsString("<i18nMessage>")));
	}

	@SuppressWarnings("deprecation")
	@Test
	public void shouldSerializeErrorMessagesInJSON() throws Exception {
		Message normal = new SimpleMessage("category", "The message");
		I18nMessage i18ned = new I18nMessage("category", "message");
		i18ned.setBundle(new SingletonResourceBundle("message", "Something else"));

		List<JsonSerializer<?>> gsonSerializers = new ArrayList<>();
		List<JsonDeserializer<?>> gsonDeserializers = new ArrayList<>();
		gsonSerializers.add(new MessageGsonConverter());

		GsonSerializerBuilder gsonBuilder = new GsonBuilderWrapper(new MockInstanceImpl<>(gsonSerializers), new MockInstanceImpl<>(gsonDeserializers), 
				new Serializee(new DefaultReflectionProvider()), new DefaultReflectionProvider());
		MockSerializationResult result = new MockSerializationResult(null, null, gsonBuilder, new DefaultReflectionProvider()) {
			@Override
			public <T extends View> T use(Class<T> view) {
				return view.cast(new DefaultRepresentationResult(new FormatResolver() {
					@Override
					public String getAcceptFormat() {
						return "json";
					}

				}, this, new MockInstanceImpl<Serialization>(super.use(JSONSerialization.class))));
			}
		};
		DefaultStatus status = new DefaultStatus(response, result, config, new JavassistProxifier(), router);

		status.badRequest(Arrays.asList(normal, i18ned));

		String serialized = result.serializedResult();
		assertThat(serialized, containsString("\"message\":\"The message\""));
		assertThat(serialized, containsString("\"category\":\"category\""));
		assertThat(serialized, containsString("\"message\":\"Something else\""));
		assertThat(serialized, not(containsString("\"validationMessage\"")));
		assertThat(serialized, not(containsString("\"i18nMessage\"")));
	}
}
