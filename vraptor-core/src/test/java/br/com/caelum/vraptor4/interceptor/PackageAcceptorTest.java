package br.com.caelum.vraptor4.interceptor;

import java.lang.reflect.AnnotatedElement;

import net.vidageek.mirror.dsl.Mirror;

import org.junit.Test;

import br.com.caelum.vraptor4.controller.BeanClass;
import br.com.caelum.vraptor4.controller.ControllerInstance;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PackageAcceptorTest {

	@Test
	public void shouldAcceptSamePackage() throws Exception {
		PackagesAcceptor acceptor = new PackagesAcceptor();
		ControllerInstance controllerInstance = mock(ControllerInstance.class);
		BeanClass beanClass = mock(BeanClass.class);
		when(beanClass.getPackageName()).thenReturn("br.com.caelum");
		when(controllerInstance.getBeanClass()).thenReturn(beanClass);
		AcceptsForPackages annotation = new Mirror()
		.on((AnnotatedElement) SinglePackageInterceptor.class)
		.reflect().annotation(AcceptsForPackages.class);		
		acceptor.initialize(annotation);
		assertTrue(acceptor.validate(null, controllerInstance));
	}
	
	@Test
	public void shouldAcceptSamePackageInMultiple() throws Exception {
		PackagesAcceptor acceptor = new PackagesAcceptor();
		ControllerInstance controllerInstance = mock(ControllerInstance.class);
		BeanClass beanClass = mock(BeanClass.class);
		when(beanClass.getPackageName()).thenReturn("br.com.caelum.controller.report");
		when(controllerInstance.getBeanClass()).thenReturn(beanClass);
		AcceptsForPackages annotation = new Mirror()
		.on((AnnotatedElement) MultiplePackageInterceptor.class)
		.reflect().annotation(AcceptsForPackages.class);		
		acceptor.initialize(annotation);
		assertTrue(acceptor.validate(null, controllerInstance));
	}
	
	@Test
	public void shouldNotAcceptDifferentPackage() throws Exception {
		PackagesAcceptor acceptor = new PackagesAcceptor();
		ControllerInstance controllerInstance = mock(ControllerInstance.class);
		BeanClass beanClass = mock(BeanClass.class);
		when(beanClass.getPackageName()).thenReturn("br.com.caelum.controller.module");
		when(controllerInstance.getBeanClass()).thenReturn(beanClass);
		AcceptsForPackages annotation = new Mirror()
		.on((AnnotatedElement) MultiplePackageInterceptor.class)
		.reflect().annotation(AcceptsForPackages.class);		
		acceptor.initialize(annotation);
		assertFalse(acceptor.validate(null, controllerInstance));
	}
	
	@Test
	public void shouldAcceptSubPackage() throws Exception {
		PackagesAcceptor acceptor = new PackagesAcceptor();
		ControllerInstance controllerInstance = mock(ControllerInstance.class);
		BeanClass beanClass = mock(BeanClass.class);
		when(beanClass.getPackageName()).thenReturn("br.com.caelum.controller.report");
		when(controllerInstance.getBeanClass()).thenReturn(beanClass);
		AcceptsForPackages annotation = new Mirror()
		.on((AnnotatedElement) SubPackageInterceptor.class)
		.reflect().annotation(AcceptsForPackages.class);		
		acceptor.initialize(annotation);
		assertTrue(acceptor.validate(null, controllerInstance));
	}
	
	@AcceptsForPackages("br.com.caelum")
	private class SinglePackageInterceptor{}
	
	@AcceptsForPackages({"br.com.caelum.controller.report","br.com.caelum.controller.financial"})
	private class MultiplePackageInterceptor{}
	
	@AcceptsForPackages("br.com.caelum.controller")
	private class SubPackageInterceptor{}
}
