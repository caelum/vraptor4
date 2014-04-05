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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.ResourceBundle;

import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.validator.Message;

public class MockValidatorTest {
	private MockValidator validator;
	private ResourceBundle bundle;

	@Before
	public void setUp() {
		validator = spy(new MockValidator());
		bundle = ResourceBundle.getBundle("messages");
	}

	@Test
	public void containsMessageShouldReturnFalseWhenExpectedMessageDontExists() {
		assertFalse(validator.containsMessage("required_field", "name"));
	}
	
	@Test
	public void containsMessageShouldReturnTrueWhenExpectedMessageExists() {
		Message message = mock(Message.class);
		
		when(message.getMessage()).thenReturn(bundle.getString("underage"));
		when(validator.getErrors()).thenReturn(Arrays.asList(message));
		
		assertTrue(validator.containsMessage("underage"));
	}
	
	@Test
	public void containsMessageShouldReturnTrueWhenExpectedMessageWithParamsExists() {
		Message message = mock(Message.class);
		
		when(message.getMessage()).thenReturn(MessageFormat.format(bundle.getString("required_field"), "name"));
		when(validator.getErrors()).thenReturn(Arrays.asList(message));
		
		assertTrue(validator.containsMessage("required_field", "name"));
	}
}
