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

import javax.validation.constraints.NotNull;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class MockBeanValidatorTest {
	private MockBeanValidator validator;

	@Before
	public void setUp() {
		validator = new MockBeanValidator();
	}

	@Test
	public void shouldHaveOneError() {
		Customer customer = new Customer(1, null);
		validator.validate(customer);
		
		Assert.assertTrue(validator.hasErrors());
		Assert.assertEquals(1, validator.getErrors().size());
	}

	@Test
	public void shouldHaveTwoErrors() {
		Customer customer = new Customer(null, null);
		validator.validate(customer);
		
		Assert.assertTrue(validator.hasErrors());
		Assert.assertEquals(2, validator.getErrors().size());
	}

	@Test
	public void shouldNotHaveErrors() {
		Customer customer = new Customer(1, "Renan Montenegro");
		validator.validate(customer);
		
		Assert.assertFalse(validator.hasErrors());
		Assert.assertTrue(validator.getErrors().isEmpty());
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
