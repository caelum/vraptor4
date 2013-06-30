package br.com.caelum.vraptor4.interceptor;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import br.com.caelum.vraptor.interceptor.InstanceContainer;
import br.com.caelum.vraptor4.controller.ControllerMethod;
import br.com.caelum.vraptor4.controller.DefaultBeanClass;
import br.com.caelum.vraptor4.controller.DefaultControllerInstance;
import br.com.caelum.vraptor4.controller.DefaultControllerMethod;
import br.com.caelum.vraptor4.interceptor.example.InterceptorWithCustomizedAccepts;
import br.com.caelum.vraptor4.interceptor.example.NotLoggedExampleController;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CustomAcceptsVerifierTest {

	@Test
	public void shouldValidateWithOne() throws Exception {
		InterceptorWithCustomizedAccepts interceptor = new InterceptorWithCustomizedAccepts();
		WithAnnotationAcceptor validator = mock(WithAnnotationAcceptor.class);		
		ControllerMethod controllerMethod = mock(ControllerMethod.class);
		DefaultControllerInstance controllerInstance = new DefaultControllerInstance(
				new NotLoggedExampleController());
		CustomiAcceptsVerifier verifier = new CustomiAcceptsVerifier(controllerMethod,
				controllerInstance, new InstanceContainer(validator), interceptor);
		when(validator.validate(controllerMethod, controllerInstance)).thenReturn(true);
		assertTrue(verifier.isValid());
	}
	
	@Test
	public void shouldValidateWithTwoOrMore() throws Exception {
		InterceptorWithCustomizedAccepts interceptor = new InterceptorWithCustomizedAccepts();
		WithAnnotationAcceptor validator1 = mock(WithAnnotationAcceptor.class);		
		PackagesAcceptor validator2 = mock(PackagesAcceptor.class);		
		ControllerMethod controllerMethod = mock(ControllerMethod.class);
		DefaultControllerInstance controllerInstance = new DefaultControllerInstance(
				new NotLoggedExampleController());
		CustomiAcceptsVerifier verifier = new CustomiAcceptsVerifier(controllerMethod,
				controllerInstance, new InstanceContainer(validator1,validator2), interceptor);
		when(validator1.validate(controllerMethod, controllerInstance)).thenReturn(true);
		when(validator2.validate(controllerMethod, controllerInstance)).thenReturn(true);
		assertTrue(verifier.isValid());
	}
	
	@Test
	public void shouldEndProcessIfOneIsInvalid() throws Exception {
		InterceptorWithCustomizedAccepts interceptor = new InterceptorWithCustomizedAccepts();
		WithAnnotationAcceptor validator1 = mock(WithAnnotationAcceptor.class);		
		PackagesAcceptor validator2 = mock(PackagesAcceptor.class);		
		ControllerMethod controllerMethod = mock(ControllerMethod.class);
		DefaultControllerInstance controllerInstance = new DefaultControllerInstance(
				new NotLoggedExampleController());
		CustomiAcceptsVerifier verifier = new CustomiAcceptsVerifier(controllerMethod,
				controllerInstance, new InstanceContainer(validator1,validator2), interceptor);
		when(validator1.validate(controllerMethod, controllerInstance)).thenReturn(false);
		when(validator2.validate(controllerMethod, controllerInstance)).thenReturn(true);
		verify(validator2,never()).validate(controllerMethod, controllerInstance);
		assertFalse(verifier.isValid());
	}	
}
