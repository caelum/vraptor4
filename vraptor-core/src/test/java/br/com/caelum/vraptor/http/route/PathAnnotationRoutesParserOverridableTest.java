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

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import br.com.caelum.vraptor.Controller;
import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.controller.DefaultBeanClass;
import br.com.caelum.vraptor.core.Converters;
import br.com.caelum.vraptor.http.EncodingHandler;
import br.com.caelum.vraptor.http.ParameterNameProvider;
import br.com.caelum.vraptor.http.ParanamerNameProvider;
import br.com.caelum.vraptor.proxy.JavassistProxifier;
import br.com.caelum.vraptor.proxy.Proxifier;

/**
 * Tests for Overridable component
 *  
 * @author Denilson Telaroli
 */
public class PathAnnotationRoutesParserOverridableTest {

	static class OverridePathAnnotationRoutesParser extends PathAnnotationRoutesParser {
		public OverridePathAnnotationRoutesParser(Router router) {
			super(router);
		}
		@Override
		protected String extractControllerNameFrom(Class<?> type) {
			return "/prefix" + super.extractControllerNameFrom(type);
		}
	}
	
	@Controller
	static class ConventionController {
		public void conventionMethod() {
		}
		@Path("/pathMethod")
		public void pathMethod() {
		}
	}
	
	@Controller @Path("/path")
	static class PathController {
		public void conventionMethod() {
		}
		@Path("/pathMethod")
		public void pathMethod() {
		}
	}

	private Proxifier proxifier;
	private @Mock Converters converters;
	private NoTypeFinder typeFinder;
	private @Mock Router router;
	private ParameterNameProvider nameProvider;
	private @Mock EncodingHandler encodingHandler;
	private OverridePathAnnotationRoutesParser parser;
	private DefaultBeanClass pathController;
	private DefaultBeanClass conventionController;
	private List<Route> rulesForPathController;
	private List<Route> rulesForConventionController;
	
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		
		this.proxifier = new JavassistProxifier();
		this.typeFinder = new NoTypeFinder();
		this.nameProvider = new ParanamerNameProvider();

		when(router.builderFor(anyString())).thenAnswer(new Answer<DefaultRouteBuilder>() {
			@Override
			public DefaultRouteBuilder answer(InvocationOnMock invocation) throws Throwable {
				return new DefaultRouteBuilder(proxifier, typeFinder, converters, nameProvider, new JavaEvaluator(), 
						(String) invocation.getArguments()[0],encodingHandler);
			}
		});
		
		parser = new OverridePathAnnotationRoutesParser(router);
		
		conventionController = new DefaultBeanClass(ConventionController.class);
		pathController = new DefaultBeanClass(PathController.class);
		
		rulesForPathController = parser.rulesFor(pathController);
		rulesForConventionController = parser.rulesFor(conventionController);
	}
	
	private Route getRouteMatching(List<Route> routes, String uri) {
		for (Route route : routes) {
			if (route.canHandle(uri)) {
				return route;
			}
		}
		return null;
	}

	@Test
	public void shouldExtractControllerNameWithPrefixFromConvention() {
		Class<?> type = conventionController.getType();
		assertThat(parser.extractControllerNameFrom(type), equalTo("/prefix/convention"));
	}
	
	@Test
	public void shouldReturnControllerNameAndMethodNameWithPrefixFromConvention() {
		Route route = getRouteMatching(rulesForConventionController, "/prefix/convention/conventionMethod");
		assertTrue(route.canHandle("/prefix/convention/conventionMethod"));
	}
	
	@Test
	public void shouldReturnMethodNameWithPrefixFromPathMethod() {
		Route route = getRouteMatching(rulesForConventionController, "/prefix/pathMethod");
		assertThat(route, notNullValue());
		assertTrue(route.canHandle("/prefix/pathMethod"));
	}
	
	@Test
	public void shouldExtractControllerNameWithPrefixFromPathAnnotation() {
		Class<?> type = pathController.getType();
		assertThat(parser.extractControllerNameFrom(type), equalTo("/prefix/path"));
	}
	
	@Test
	public void shouldReturnControllerNameAndMethodNameWithPrefixFromPathAnnotation() {
		Route route = getRouteMatching(rulesForPathController, "/prefix/path/conventionMethod");
		assertTrue(route.canHandle("/prefix/path/conventionMethod"));
	}
	
	@Test
	public void shouldReturnControllerNameAndMethodNameWithPrefixFromPathAnnotationMethod() {
		Route route = getRouteMatching(rulesForPathController, "/prefix/path/pathMethod");
		assertThat(route, notNullValue());
		assertTrue(route.canHandle("/prefix/path/pathMethod"));
	}
}