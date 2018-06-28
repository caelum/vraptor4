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
package br.com.caelum.vraptor.interceptor;

import static br.com.caelum.vraptor.interceptor.CustomAcceptsVerifier.getCustomAcceptsAnnotations;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.annotation.Annotation;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.com.caelum.vraptor.controller.ControllerInstance;
import br.com.caelum.vraptor.controller.ControllerMethod;
import br.com.caelum.vraptor.controller.DefaultControllerInstance;
import br.com.caelum.vraptor.interceptor.example.InterceptorWithCustomizedAccepts;
import br.com.caelum.vraptor.interceptor.example.MethodLevelAcceptsController;

public class CustomAcceptsVerifierTest {

	private @Mock WithAnnotationAcceptor withAnnotationAcceptor;
	private @Mock ControllerMethod controllerMethod;
	private @Mock PackagesAcceptor packagesAcceptor;
	private InterceptorWithCustomizedAccepts interceptor;
	private ControllerInstance controllerInstance;
	private List<Annotation> constraints;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		interceptor = new InterceptorWithCustomizedAccepts();
		controllerInstance = new DefaultControllerInstance(new MethodLevelAcceptsController());
		constraints = getCustomAcceptsAnnotations(interceptor.getClass());
		when(withAnnotationAcceptor.validate(controllerMethod, controllerInstance)).thenReturn(true);
	}

	@Test
	public void shouldValidateWithOne() throws Exception {
		InstanceContainer container = new InstanceContainer(withAnnotationAcceptor);
		CustomAcceptsVerifier verifier = new CustomAcceptsVerifier(container);
		assertTrue(verifier.isValid(interceptor, controllerMethod, controllerInstance, constraints));
	}

	@Test
	public void shouldValidateWithTwoOrMore() throws Exception {
		InstanceContainer container = new InstanceContainer(withAnnotationAcceptor,packagesAcceptor);
		CustomAcceptsVerifier verifier = new CustomAcceptsVerifier(container);
		assertTrue(verifier.isValid(interceptor, controllerMethod, controllerInstance, constraints));
	}

	@Test
	public void shouldEndProcessIfOneIsInvalid() throws Exception {
		InstanceContainer container = new InstanceContainer(withAnnotationAcceptor,packagesAcceptor);
		CustomAcceptsVerifier verifier = new CustomAcceptsVerifier(container);
		when(withAnnotationAcceptor.validate(controllerMethod, controllerInstance)).thenReturn(false);
		verify(packagesAcceptor, never()).validate(controllerMethod, controllerInstance);
		assertFalse(verifier.isValid(interceptor, controllerMethod, controllerInstance, constraints));
	}
}
