package br.com.caelum.vraptor4.interceptor;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.com.caelum.vraptor.interceptor.InstanceContainer;
import br.com.caelum.vraptor4.interceptor.example.InterceptorWithCustomizedAccepts;
import br.com.caelum.vraptor4.interceptor.example.MethodLevelAcceptsController;
import br.com.caelum.vraptor4x.controller.ControllerInstance;
import br.com.caelum.vraptor4x.controller.ControllerMethod;
import br.com.caelum.vraptor4x.controller.DefaultControllerInstance;
import br.com.caelum.vraptor4x.interceptor.CustomAcceptsVerifier;
import br.com.caelum.vraptor4x.interceptor.PackagesAcceptor;
import br.com.caelum.vraptor4x.interceptor.WithAnnotationAcceptor;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CustomAcceptsVerifierTest {
	
	private @Mock WithAnnotationAcceptor withAnnotationAcceptor;
	private @Mock ControllerMethod controllerMethod;
	private @Mock PackagesAcceptor packagesAcceptor;
	private InterceptorWithCustomizedAccepts interceptor;
	private ControllerInstance controllerInstance;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		interceptor = new InterceptorWithCustomizedAccepts();
		controllerInstance = new DefaultControllerInstance(
				new MethodLevelAcceptsController());		
	}	

	@Test
	public void shouldValidateWithOne() throws Exception {
		CustomAcceptsVerifier verifier = new CustomAcceptsVerifier(controllerMethod,
				controllerInstance, new InstanceContainer(withAnnotationAcceptor), interceptor);		
		when(withAnnotationAcceptor.validate(controllerMethod, controllerInstance)).thenReturn(true);
		
		assertTrue(verifier.isValid());
	}
	
	@Test
	public void shouldValidateWithTwoOrMore() throws Exception {
		CustomAcceptsVerifier verifier = new CustomAcceptsVerifier(controllerMethod,
				controllerInstance, new InstanceContainer(withAnnotationAcceptor,packagesAcceptor), interceptor);
		when(withAnnotationAcceptor.validate(controllerMethod, controllerInstance)).thenReturn(true);
		when(packagesAcceptor.validate(controllerMethod, controllerInstance)).thenReturn(true);
		
		assertTrue(verifier.isValid());
	}
	
	@Test
	public void shouldEndProcessIfOneIsInvalid() throws Exception {
		CustomAcceptsVerifier verifier = new CustomAcceptsVerifier(controllerMethod,
				controllerInstance, new InstanceContainer(withAnnotationAcceptor,packagesAcceptor), interceptor);
		when(withAnnotationAcceptor.validate(controllerMethod, controllerInstance)).thenReturn(false);
		when(packagesAcceptor.validate(controllerMethod, controllerInstance)).thenReturn(true);
		
		verify(packagesAcceptor,never()).validate(controllerMethod, controllerInstance);
		assertFalse(verifier.isValid());
	}	
}
