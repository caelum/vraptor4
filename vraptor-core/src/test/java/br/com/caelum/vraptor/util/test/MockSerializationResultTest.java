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

import static br.com.caelum.vraptor.view.Results.json;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class MockSerializationResultTest {

	private MockSerializationResult result;

	@Before
	public void setUp() throws Exception {
		result = new MockSerializationResult();
	}

	public static class Car {
		String licensePlate;
		String owner;
		String make;
		String model;

		public Car(String licensePlate,	String owner, String make,String model) {
			this.licensePlate = licensePlate;
			this.owner = owner;
			this.make = make;
			this.model = model;
		}
	}

	@Test
	public void shouldReturnStringWithObjectSerialized() throws Exception {
		Car car = new Car("XXU-5569", "Caelum", "VW", "Polo");
		String expectedResult = "{\"car\":{\"licensePlate\":\"XXU-5569\",\"owner\":\"Caelum\",\"make\":\"VW\",\"model\":\"Polo\"}}";
		result.use(json()).from(car).serialize();
		Assert.assertThat(result.serializedResult(), is(equalTo(expectedResult)));
	}
}
