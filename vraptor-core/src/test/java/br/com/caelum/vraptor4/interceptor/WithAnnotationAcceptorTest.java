package br.com.caelum.vraptor4.interceptor;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;

import javax.enterprise.util.AnnotationLiteral;

import net.vidageek.mirror.dsl.Mirror;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.com.caelum.vraptor4.interceptor.AcceptsWithAnnotations;
import br.com.caelum.vraptor4.interceptor.WithAnnotationAcceptor;
import br.com.caelum.vraptor4.interceptor.example.ClassLevelAcceptsController;
import br.com.caelum.vraptor4.interceptor.example.InterceptorWithCustomizedAccepts;
import br.com.caelum.vraptor4.interceptor.example.MethodLevelAcceptsController;
import br.com.caelum.vraptor4.interceptor.example.NotLogged;
import br.com.caelum.vraptor4.restfulie.controller.ControllerMethod;
import br.com.caelum.vraptor4.restfulie.controller.DefaultControllerInstance;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import static org.mockito.Mockito.when;

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
