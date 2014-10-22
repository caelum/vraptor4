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

package br.com.caelum.vraptor.http.route;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;
import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.com.caelum.vraptor.TwoWayConverter;
import br.com.caelum.vraptor.core.Converters;
import br.com.caelum.vraptor.core.DefaultReflectionProvider;
import br.com.caelum.vraptor.http.EncodingHandler;
import br.com.caelum.vraptor.http.MutableRequest;
import br.com.caelum.vraptor.http.ParameterNameProvider;
import br.com.caelum.vraptor.http.ParanamerNameProvider;

public class DefaultParametersControlTest {

	private @Mock MutableRequest request;
	private @Mock Converters converters;
	private @Mock TwoWayConverter converter;
	private @Mock EncodingHandler encodingHandler;
	private Evaluator evaluator;
	private ParameterNameProvider nameProvider;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		when(encodingHandler.getEncoding()).thenReturn("UTF-8");
		evaluator = new JavaEvaluator(new DefaultReflectionProvider());
		nameProvider = new ParanamerNameProvider();
	}

	@Test
	public void registerExtraParametersFromAcessedUrl() throws SecurityException, NoSuchMethodException {
		DefaultParametersControl control = getDefaultParameterControlForUrl("/clients/{dog.id}");
		control.fillIntoRequest("/clients/45", request);
		verify(request).setParameter("dog.id", new String[] {"45"});
	}

	private DefaultParametersControl getDefaultParameterControlForUrl(String url) {
		return new DefaultParametersControl(url, converters, evaluator,encodingHandler);
	}

	@Test
	public void registerParametersWithAsterisks() throws SecurityException, NoSuchMethodException {
		DefaultParametersControl control = getDefaultParameterControlForUrl("/clients/{my.path*}");
		control.fillIntoRequest("/clients/one/path", request);
		verify(request).setParameter("my.path", new String[] {"one/path"});
	}

	@Test
	public void registerParametersWithRegexes() throws SecurityException, NoSuchMethodException {
		DefaultParametersControl control = getDefaultParameterControlForUrl("/clients/{hexa:[0-9A-Z]+}");

		control.fillIntoRequest("/clients/FAF323", request);

		verify(request).setParameter("hexa", new String[] {"FAF323"});
	}

	@Test
	public void registerParametersWithMultipleRegexes() throws SecurityException, NoSuchMethodException {
		DefaultParametersControl control = new DefaultParametersControl("/test/{hash1:[a-z0-9]{16}}{id}{hash2:[a-z0-9]{16}}/", Collections.singletonMap("id", "\\d+"), converters, evaluator,encodingHandler);

		control.fillIntoRequest("/test/0123456789abcdef1234fedcba9876543210/", request);

		verify(request).setParameter("hash1", new String[] {"0123456789abcdef"});
		verify(request).setParameter("id", new String[] {"1234"});
		verify(request).setParameter("hash2", new String[] {"fedcba9876543210"});
	}

	@Test
	public void worksAsRegexWhenUsingParameters() throws SecurityException, NoSuchMethodException {
		DefaultParametersControl control = getDefaultParameterControlForUrl("/clients/{dog.id}");
		assertThat(control.matches("/clients/15"), is(equalTo(true)));
	}

	@Test
	public void worksWithBasicRegexEvaluation() throws SecurityException, NoSuchMethodException {
		DefaultParametersControl control = getDefaultParameterControlForUrl("/clients.*");
		assertThat(control.matches("/clientsWhatever"), is(equalTo(true)));
	}

	class Client {
		private final Long id;
		private Client child;

		public Client(Long id) {
			this.id = id;
		}

		public Client getChild() {
			return child;
		}

		public Long getId() {
			return id;
		}
	}

	@Test
	public void shouldTranslateAsteriskAsEmpty() throws Exception {
		Method method = Controller.class.getDeclaredMethod("store", Client.class);
		
		String uri = getDefaultParameterControlForUrl("/clients/.*").fillUri(nameProvider.parametersFor(method), client(3L));
		assertThat(uri, is(equalTo("/clients/")));
	}

	@Test
	public void shouldTranslatePatternArgs() throws Exception {
		Method method = Controller.class.getDeclaredMethod("store", Client.class);
		
		String uri = getDefaultParameterControlForUrl("/clients/{client.id}").fillUri(nameProvider.parametersFor(method), client(3L));
		assertThat(uri, is(equalTo("/clients/3")));
	}

	@Test
	public void shouldTranslatePatternArgsWithRegex() throws Exception {
		Method method = Controller.class.getDeclaredMethod("show", Long.class);
		String uri = getDefaultParameterControlForUrl("/clients/{id:[0-9]{1,}}").fillUri(nameProvider.parametersFor(method), 30L);
		assertThat(uri, is(equalTo("/clients/30")));
	}

	@Test
	public void shouldTranslatePatternArgsWithMultipleRegexes() throws Exception {
		Method method = Controller.class.getDeclaredMethod("mregex", String.class, String.class, String.class);
		
		String uri = getDefaultParameterControlForUrl("/test/{hash1:[a-z0-9]{16}}{id}{hash2:[a-z0-9]{16}}/")
				.fillUri(nameProvider.parametersFor(method), "0123456789abcdef", "1234", "fedcba9876543210");
		assertThat(uri, is(equalTo("/test/0123456789abcdef1234fedcba9876543210/")));
	}

	@Test
	public void shouldTranslatePatternArgNullAsEmpty() throws Exception {
		Method method = Controller.class.getDeclaredMethod("store", Client.class);
		
		String uri = getDefaultParameterControlForUrl("/clients/{client.id}")
				.fillUri(nameProvider.parametersFor(method), client(null));
		assertThat(uri, is(equalTo("/clients/")));
	}

	@Test
	public void shouldUseConverterIfItExists() throws Exception {
		Method method = Controller.class.getDeclaredMethod("store", Client.class);
		
		when(converters.existsTwoWayFor(Client.class)).thenReturn(true);
		when(converters.twoWayConverterFor(Client.class)).thenReturn(converter);
		when(converter.convert(any(Client.class))).thenReturn("john");

		String uri = getDefaultParameterControlForUrl("/clients/{client}")
				.fillUri(nameProvider.parametersFor(method), client(null));
		assertThat(uri, is(equalTo("/clients/john")));

	}

	@Test
	public void shouldTranslatePatternArgInternalNullAsEmpty() throws Exception {
		Method method = Controller.class.getDeclaredMethod("store", Client.class);
		String uri = getDefaultParameterControlForUrl("/clients/{client.child.id}")
				.fillUri(nameProvider.parametersFor(method), client(null));
		assertThat(uri, is(equalTo("/clients/")));
	}

	@Test
	public void shouldMatchPatternLazily() throws Exception {
		DefaultParametersControl wrong = getDefaultParameterControlForUrl("/clients/{client.id}/");
		DefaultParametersControl right = getDefaultParameterControlForUrl("/clients/{client.id}/subtask/");
		String uri = "/clients/3/subtask/";

		assertThat(wrong.matches(uri), is(false));
		assertThat(right.matches(uri), is(true));

	}

	@Test
	public void shouldMatchMoreThanOneVariable() throws Exception {
		DefaultParametersControl control = getDefaultParameterControlForUrl("/clients/{client.id}/subtask/{task.id}/");

		assertThat(control.matches("/clients/3/subtask/5/"), is(true));
	}
	private Client client(Long id) {
		return new Client(id);
	}

	@Test
	public void shouldBeGreedyWhenIPutAnAsteriskOnExpression() throws Exception {
		DefaultParametersControl control = getDefaultParameterControlForUrl("/clients/{pathToFile*}");

		assertThat(control.matches("/clients/my/path/to/file/"), is(true));
	}
	@Test
	public void shouldNotBeGreedyAtPatternCompiling() throws Exception {
		DefaultParametersControl control = getDefaultParameterControlForUrl("/project/{project.name}/build/{buildId}/view/{filename*}");

		String uri = "/project/Vraptor3/build/12345/view/artifacts/vraptor.jar";
		assertThat(control.matches(uri), is(true));

		control.fillIntoRequest(uri, request);

		verify(request).setParameter("project.name", "Vraptor3");
		verify(request).setParameter("filename", new String[] {"artifacts/vraptor.jar"});
		verify(request).setParameter("buildId", new String[] {"12345"});
		assertThat(control.apply(new String[] {"Vraptor3", "12345", "artifacts/vraptor.jar"}),
				is(uri));
	}

	@Test
	public void registerExtraParametersFromAcessedUrlWithGreedyParameters() throws SecurityException, NoSuchMethodException {
		DefaultParametersControl control = getDefaultParameterControlForUrl("/clients/{pathToFile*}");

		control.fillIntoRequest("/clients/my/path/to/file", request);

		verify(request).setParameter("pathToFile", new String[] {"my/path/to/file"});
	}

	@Test
	public void registerExtraParametersFromAcessedUrlWithGreedyAndDottedParameters() throws SecurityException, NoSuchMethodException {
		DefaultParametersControl control = getDefaultParameterControlForUrl("/clients/{path.to.file*}");

		control.fillIntoRequest("/clients/my/path/to/file", request);

		verify(request).setParameter("path.to.file", new String[] {"my/path/to/file"});
	}

	static class PathToFile {

		public void withPath(String pathToFile) {

		}
	}
	@Test
	public void fillURLWithGreedyParameters() throws SecurityException, NoSuchMethodException {
		when(encodingHandler.getEncoding()).thenReturn(null);
		DefaultParametersControl control = getDefaultParameterControlForUrl("/clients/{pathToFile*}");

		Method method = Controller.class.getDeclaredMethod("pathToFile", String.class);
		String filled = control.fillUri(nameProvider.parametersFor(method), "my/path/to/file");

		assertThat(filled, is("/clients/my/path/to/file"));
	}
	@Test
	public void fillURLWithoutGreedyParameters() throws SecurityException, NoSuchMethodException {
		when(encodingHandler.getEncoding()).thenReturn(null);
		DefaultParametersControl control = getDefaultParameterControlForUrl("/clients/{pathToFile}");
		Method method = Controller.class.getDeclaredMethod("pathToFile", String.class);

		String filled = control.fillUri(nameProvider.parametersFor(method), "my/path/to/file");

		assertThat(filled, is("/clients/my/path/to/file"));
	}

	@Test
	public void whenNoParameterPatternsAreGivenShouldMatchAnything() throws Exception {
		ParametersControl control = new DefaultParametersControl("/any/{aParameter}/what", Collections.<String,String>emptyMap(), converters, evaluator,encodingHandler);
		assertTrue(control.matches("/any/ICanPutAnythingInHere/what"));
	}
	@Test
	public void whenParameterPatternsAreGivenShouldMatchAccordingToGivenPatterns() throws Exception {
		ParametersControl control = new DefaultParametersControl("/any/{aParameter}/what",
				Collections.singletonMap("aParameter", "aaa\\d{3}bbb"), converters, evaluator,encodingHandler);
		assertFalse(control.matches("/any/ICantPutAnythingInHere/what"));
		assertFalse(control.matches("/any/aaa12bbb/what"));
		assertTrue(control.matches("/any/aaa123bbb/what"));
	}

	@Test
	public void shouldFillRequestWhenAPatternIsSpecified() throws Exception {
		DefaultParametersControl control = new DefaultParametersControl("/project/{project.id}/build/",
				Collections.singletonMap("project.id", "\\d+"), converters, evaluator,encodingHandler);

		String uri = "/project/15/build/";
		assertThat(control.matches(uri), is(true));

		control.fillIntoRequest(uri, request);

		verify(request).setParameter("project.id", "15");

		assertThat(control.apply(new String[] {"15"}),is(uri));
	}

	@Test
	public void shouldDecodeUriParameters() throws Exception {
		DefaultParametersControl control = getDefaultParameterControlForUrl("/clients/{name}");

		control.fillIntoRequest("/clients/Joao+Leno", request);

		verify(request).setParameter("name", "Joao Leno");

		control.fillIntoRequest("/clients/Paulo%20Macartinei", request);

		verify(request).setParameter("name", "Paulo Macartinei");
	}

	@Test
	public void shouldEncodeUriParameters() throws Exception {
		Method method = Controller.class.getDeclaredMethod("lang", String.class);
		when(encodingHandler.getEncoding()).thenReturn("UTF-8");
		String uri = getDefaultParameterControlForUrl("/language/{lang}/about")
				.fillUri(nameProvider.parametersFor(method), "c#");
		assertThat(uri, is(equalTo("/language/c%23/about")));
	}

	public static class Controller {
		void lang(String lang) {}
		void show(Long id) {}
		void store(Client client) {}
		void pathToFile(String pathToFile) {}
		void mregex(String hash1, String id, String hash2) {}
	}

}
