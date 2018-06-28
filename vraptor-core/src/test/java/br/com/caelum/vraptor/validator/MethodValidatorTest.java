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
package br.com.caelum.vraptor.validator;

import static br.com.caelum.vraptor.controller.DefaultControllerMethod.instanceFor;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import java.lang.reflect.Method;
import java.util.Locale;

import javax.validation.MessageInterpolator;
import javax.validation.ValidatorFactory;
import javax.validation.constraints.Min;

import org.hibernate.validator.constraints.Email;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import br.com.caelum.vraptor.controller.ControllerMethod;
import br.com.caelum.vraptor.controller.DefaultControllerInstance;
import br.com.caelum.vraptor.core.MethodInfo;
import br.com.caelum.vraptor.events.MethodReady;
import br.com.caelum.vraptor.http.ParanamerNameProvider;
import br.com.caelum.vraptor.util.test.MockInstanceImpl;
import br.com.caelum.vraptor.util.test.MockValidator;
import br.com.caelum.vraptor.validator.beanvalidation.MessageInterpolatorFactory;
import br.com.caelum.vraptor.validator.beanvalidation.MethodValidator;

/**
 * Test method validator feature.
 * 
 * @author Otávio Scherer Garcia
 * @author Rodrigo Turini
 * @since 3.5
 */
public class MethodValidatorTest {

	private Validator validator;
	private ValidatorFactory validatorFactory;
	private MessageInterpolator interpolator;

	private ControllerMethod withConstraint;
	private ControllerMethod withoutConstraint;
	private ControllerMethod withoutConstraintAndDomainObject;
	private DefaultControllerInstance instance;

	private MethodInfo methodInfo = new MethodInfo(new ParanamerNameProvider());

	@Before
	public void setup() throws Exception {
		MockitoAnnotations.initMocks(this);
		Locale.setDefault(Locale.ENGLISH);
		validatorFactory = javax.validation.Validation.buildDefaultValidatorFactory();
		interpolator = new MessageInterpolatorFactory(validatorFactory).getInstance();
		validator = new MockValidator();
		withConstraint = instanceFor(MyController.class, getMethod("withConstraint",String.class));
		withoutConstraint = instanceFor(MyController.class, getMethod("withoutConstraint",String.class));
		withoutConstraintAndDomainObject = instanceFor(MyController.class, getMethod("withoutConstraintAndDomainObject",Example.class));
		instance = new DefaultControllerInstance(new MyController());
	}

	private Method getMethod(String methodName,Class<?> parameterClass) throws NoSuchMethodException {
		return MyController.class.getMethod(methodName, parameterClass);
	}

	@Test
	public void shouldAcceptIfMethodHasConstraint() {
		methodInfo.setControllerMethod(withConstraint);

		DefaultControllerInstance controller = spy(instance);
		getMethodValidator().validate(new MethodReady(withConstraint), controller, methodInfo, validator);
		verify(controller).getController();
	}

	@Test
	public void shouldNotAcceptIfMethodHasConstraint() {
		DefaultControllerInstance controller = spy(instance);
		getMethodValidator().validate(new MethodReady(withoutConstraint), controller, methodInfo, validator);
		verify(controller, never()).getController();
	}
	
	@Test
	public void shouldNotAcceptIfMethodDoesNotHaveConstraintAndHasDomainObjectParameter() {
		DefaultControllerInstance controller = spy(instance);
		getMethodValidator().validate(new MethodReady(withoutConstraintAndDomainObject), controller, methodInfo, validator);
		verify(controller, never()).getController();
	}

	@Test
	public void shouldValidateMethodWithConstraint() throws Exception {
		methodInfo.setControllerMethod(withConstraint);
		methodInfo.setParameter(0, "a");

		Message[] expected = { new SimpleMessage("email", "deve ser maior ou igual a 10"),
				new SimpleMessage("email", "Não é um endereço de e-mail") };

		getMethodValidator().validate(new MethodReady(withConstraint), instance, methodInfo, validator);
		assertThat(validator.getErrors(), hasSize(2));
		assertThat(validator.getErrors(), containsInAnyOrder(expected));
	}

	private MethodValidator getMethodValidator() {
		return new MethodValidator(new MockInstanceImpl<>(new Locale("pt", "br")), interpolator, validatorFactory.getValidator());
	}
	
	public class Example {
		private int number;
		private String name;

		public int getNumber() {
			return number;
		}

		public void setNumber(int number) {
			this.number = number;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}		
	}
	
	public class MyController {
		
		public void withConstraint(@Min(10) @Email String email) {
		}

		public void withoutConstraint(String foo) {
		}

		public void withoutConstraintAndDomainObject(Example example) {
		}
	}

}
