package br.com.caelum.vraptor.interceptor;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.lang.reflect.AnnotatedElement;

import net.vidageek.mirror.dsl.Mirror;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.com.caelum.vraptor.controller.BeanClass;
import br.com.caelum.vraptor.controller.ControllerInstance;

public class PackageAcceptorTest {
	
	private @Mock ControllerInstance controllerInstance;
	private @Mock BeanClass beanClass;	
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		when(controllerInstance.getBeanClass()).thenReturn(beanClass);
	}	

	@Test
	public void shouldAcceptSamePackage() throws Exception {
		PackagesAcceptor acceptor = new PackagesAcceptor();
		AcceptsForPackages annotation = new Mirror()
		.on((AnnotatedElement) SinglePackageInterceptor.class)
		.reflect().annotation(AcceptsForPackages.class);		
		
		when(beanClass.getPackageName()).thenReturn("br.com.caelum");
		
		acceptor.initialize(annotation);
		
		assertTrue(acceptor.validate(null, controllerInstance));
	}
	
	@Test
	public void shouldAcceptSamePackageInMultiple() throws Exception {
		PackagesAcceptor acceptor = new PackagesAcceptor();
		AcceptsForPackages annotation = new Mirror()
		.on((AnnotatedElement) MultiplePackageInterceptor.class)
		.reflect().annotation(AcceptsForPackages.class);
		
		when(beanClass.getPackageName()).thenReturn("br.com.caelum.controller.report");
		
		acceptor.initialize(annotation);
		
		assertTrue(acceptor.validate(null, controllerInstance));
	}
	
	@Test
	public void shouldNotAcceptDifferentPackage() throws Exception {
		PackagesAcceptor acceptor = new PackagesAcceptor();
		AcceptsForPackages annotation = new Mirror()
		.on((AnnotatedElement) MultiplePackageInterceptor.class)
		.reflect().annotation(AcceptsForPackages.class);		
		
		when(beanClass.getPackageName()).thenReturn("br.com.caelum.controller.module");
		
		acceptor.initialize(annotation);
		
		assertFalse(acceptor.validate(null, controllerInstance));
	}
	
	@Test
	public void shouldAcceptSubPackage() throws Exception {
		PackagesAcceptor acceptor = new PackagesAcceptor();
		AcceptsForPackages annotation = new Mirror()
		.on((AnnotatedElement) SubPackageInterceptor.class)
		.reflect().annotation(AcceptsForPackages.class);
		
		when(beanClass.getPackageName()).thenReturn("br.com.caelum.controller.report");
		
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
