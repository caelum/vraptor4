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
package br.com.caelum.vraptor.util.test;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

import javax.validation.constraints.NotNull;

import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.validator.SimpleMessage;

public class MockBeanValidatorTest {
	private MockBeanValidator validator;

	@Before
	public void setUp() {
		validator = new MockBeanValidator();
	}

	@Test
	public void shouldHaveOneErrorForInvalidBean() {
		validator.validate(new Customer(1, null));
		assertThat(validator.getErrors(), hasSize(1));
	}

	@Test
	public void shouldHaveTwoErrorsForInvalidBean() {
		validator.validate(new Customer(null, null));
		assertThat(validator.getErrors(), hasSize(2));
	}

	@Test
	public void shouldHaveNoErrorsForValidBean() {
		validator.validate(new Customer(1, "Fulano"));
		assertThat(validator.getErrors(), hasSize(0));
	}

	@Test
	public void shouldHaveOneErrorEvenValidationFromVRaptor() {
		Customer customer = new Customer(1, null);
		validator.addIf(customer.name == null, new SimpleMessage("nome", "Nome não pode ser nulo."));
		assertThat(validator.getErrors(), hasSize(1));
	}

	@Test
	public void shouldHaveTwoErrorsEvenValidationFromVRaptor() {
		Customer customer = new Customer(null, null);
		validator.addIf(customer.id == null, new SimpleMessage("id", "Id não pode ser nulo."));
		validator.addIf(customer.name == null, new SimpleMessage("nome", "Nome não pode ser nulo."));
		assertThat(validator.getErrors(), hasSize(2));
	}

	@Test
	public void shouldHaveNoErrorsEvenValidationFromValidBean() {
		Customer customer = new Customer(1, "Fulano");
		validator.addIf(customer.id == null, new SimpleMessage("id", "Id não pode ser nulo."));
		validator.addIf(customer.name == null, new SimpleMessage("nome", "Nome não pode ser nulo."));
		assertThat(validator.getErrors(), hasSize(0));
	}
	
	public static class Customer {

		@NotNull public Integer id;
		@NotNull public String name;

		public Customer(Integer id, String name) {
			this.id = id;
			this.name = name;
		}
	}
	
}
