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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;

import javax.enterprise.util.AnnotationLiteral;

import net.vidageek.mirror.dsl.Mirror;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.com.caelum.vraptor.controller.ControllerMethod;
import br.com.caelum.vraptor.controller.DefaultControllerInstance;
import br.com.caelum.vraptor.interceptor.example.ClassLevelAcceptsController;
import br.com.caelum.vraptor.interceptor.example.InterceptorWithCustomizedAccepts;
import br.com.caelum.vraptor.interceptor.example.MethodLevelAcceptsController;
import br.com.caelum.vraptor.interceptor.example.NotLogged;

public class WithAnnotationAcceptorTest {
	
	private @Mock ControllerMethod controllerMethod;
	private AcceptsWithAnnotations annotation;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		annotation = new Mirror()
		.on((AnnotatedElement) InterceptorWithCustomizedAccepts.class)
		.reflect().annotation(AcceptsWithAnnotations.class);
	}	

	@Test
	public void shouldAcceptMethodWithAnnotation() throws Exception {
		WithAnnotationAcceptor acceptor = new WithAnnotationAcceptor();		
		DefaultControllerInstance controllerInstance = new DefaultControllerInstance(new MethodLevelAcceptsController());
		AnnotationLiteral<NotLogged> notLogged = new AnnotationLiteral<NotLogged>() {};
		
		acceptor.initialize(annotation);
		
		when(controllerMethod.getAnnotations()).thenReturn(new Annotation[]{notLogged});
		
		assertTrue(acceptor.validate(controllerMethod, controllerInstance));
	}
	
	@Test
	public void shouldNotAcceptMethodWithoutAnnotation() throws Exception {
		WithAnnotationAcceptor acceptor = new WithAnnotationAcceptor();
		DefaultControllerInstance controllerInstance = new DefaultControllerInstance(new MethodLevelAcceptsController());
		
		acceptor.initialize(annotation);
		
		when(controllerMethod.getAnnotations()).thenReturn(new Annotation[]{});
		
		assertFalse(acceptor.validate(controllerMethod, controllerInstance));
	}
	
	@Test
	public void shouldAcceptsAllMethodsInsideAnnotatedClass() throws Exception {
		WithAnnotationAcceptor acceptor = new WithAnnotationAcceptor();
		DefaultControllerInstance controllerInstance = new DefaultControllerInstance(new ClassLevelAcceptsController());
		
		acceptor.initialize(annotation);
		
		when(controllerMethod.getAnnotations()).thenReturn(new Annotation[]{});
		
		assertTrue(acceptor.validate(controllerMethod, controllerInstance));
	}	
	
	@Test
	public void shouldNotAcceptsAnyMethodsInsideNonAnnotatedClass() throws Exception {
		WithAnnotationAcceptor acceptor = new WithAnnotationAcceptor();
		DefaultControllerInstance controllerInstance = new DefaultControllerInstance(new Object());
		
		acceptor.initialize(annotation);
		
		when(controllerMethod.getAnnotations()).thenReturn(new Annotation[]{});
		
		assertFalse(acceptor.validate(controllerMethod, controllerInstance));
	}	
}
