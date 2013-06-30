package br.com.caelum.vraptor4.interceptor;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;

import javax.enterprise.util.AnnotationLiteral;

import net.vidageek.mirror.dsl.Mirror;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import br.com.caelum.vraptor4.controller.ControllerMethod;
import br.com.caelum.vraptor4.controller.DefaultControllerInstance;
import br.com.caelum.vraptor4.interceptor.example.ClassLevelAcceptsController;
import br.com.caelum.vraptor4.interceptor.example.InterceptorWithCustomizedAccepts;
import br.com.caelum.vraptor4.interceptor.example.MethodLevelAcceptsController;
import br.com.caelum.vraptor4.interceptor.example.NotLogged;

public class WithAnnotationAcceptorTest {

	@Test
	public void shouldAcceptMethodWithAnnotation() throws Exception {
		WithAnnotationAcceptor acceptor = new WithAnnotationAcceptor();
		AcceptsWithAnnotations annotation = new Mirror()
				.on((AnnotatedElement) InterceptorWithCustomizedAccepts.class)
				.reflect().annotation(AcceptsWithAnnotations.class);
		acceptor.initialize(annotation);
		DefaultControllerInstance controllerInstance = new DefaultControllerInstance(new MethodLevelAcceptsController());
		ControllerMethod controllerMethod = mock(ControllerMethod.class);
		AnnotationLiteral<NotLogged> notLogged = new AnnotationLiteral<NotLogged>() {};
		when(controllerMethod.getAnnotations()).thenReturn(new Annotation[]{notLogged});
		assertTrue(acceptor.validate(controllerMethod, controllerInstance));
	}
	
	@Test
	public void shouldNotAcceptMethodWithoutAnnotation() throws Exception {
		WithAnnotationAcceptor acceptor = new WithAnnotationAcceptor();
		AcceptsWithAnnotations annotation = new Mirror()
				.on((AnnotatedElement) InterceptorWithCustomizedAccepts.class)
				.reflect().annotation(AcceptsWithAnnotations.class);
		acceptor.initialize(annotation);
		DefaultControllerInstance controllerInstance = new DefaultControllerInstance(new MethodLevelAcceptsController());
		ControllerMethod controllerMethod = mock(ControllerMethod.class);		
		when(controllerMethod.getAnnotations()).thenReturn(new Annotation[]{});
		assertFalse(acceptor.validate(controllerMethod, controllerInstance));
	}
	
	@Test
	public void shouldAcceptsAllMethodsInsideAnnotatedClass() throws Exception {
		WithAnnotationAcceptor acceptor = new WithAnnotationAcceptor();
		AcceptsWithAnnotations annotation = new Mirror()
		.on((AnnotatedElement) InterceptorWithCustomizedAccepts.class)
		.reflect().annotation(AcceptsWithAnnotations.class);
		acceptor.initialize(annotation);
		DefaultControllerInstance controllerInstance = new DefaultControllerInstance(new ClassLevelAcceptsController());
		ControllerMethod controllerMethod = mock(ControllerMethod.class);		
		when(controllerMethod.getAnnotations()).thenReturn(new Annotation[]{});
		assertTrue(acceptor.validate(controllerMethod, controllerInstance));
	}	
	
	@Test
	public void shouldNotAcceptsAnyMethodsInsideNonAnnotatedClass() throws Exception {
		WithAnnotationAcceptor acceptor = new WithAnnotationAcceptor();
		AcceptsWithAnnotations annotation = new Mirror()
		.on((AnnotatedElement) InterceptorWithCustomizedAccepts.class)
		.reflect().annotation(AcceptsWithAnnotations.class);
		acceptor.initialize(annotation);
		DefaultControllerInstance controllerInstance = new DefaultControllerInstance(new Object());
		ControllerMethod controllerMethod = mock(ControllerMethod.class);		
		when(controllerMethod.getAnnotations()).thenReturn(new Annotation[]{});
		assertFalse(acceptor.validate(controllerMethod, controllerInstance));
	}	
}
