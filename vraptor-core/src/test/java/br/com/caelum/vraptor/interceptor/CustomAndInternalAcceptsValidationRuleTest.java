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
package br.com.caelum.vraptor.interceptor;

import java.lang.reflect.Method;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import br.com.caelum.vraptor.Accepts;
import br.com.caelum.vraptor.core.DefaultReflectionProvider;
import br.com.caelum.vraptor.interceptor.example.NotLogged;

public class CustomAndInternalAcceptsValidationRuleTest {

	@Rule
	public ExpectedException exception = ExpectedException.none();

	private CustomAndInternalAcceptsValidationRule validationRule;
	private StepInvoker stepInvoker;

	@Before
	public void setUp() {
		stepInvoker = new StepInvoker(new DefaultReflectionProvider());
		validationRule = new CustomAndInternalAcceptsValidationRule(stepInvoker);
	}

	@Test
	public void mustNotUseInternalAcceptsAndCustomAccepts(){
		exception.expect(IllegalStateException.class);
		exception.expectMessage("Interceptor class " + InternalAndCustomAcceptsInterceptor.class.getName() + " must declare internal accepts or custom, not both");
		
		Class<?> type = InternalAndCustomAcceptsInterceptor.class;
		List<Method> methods = stepInvoker.findAllMethods(type);
		validationRule.validate(type, methods);
	}

	@Test
	public void shouldValidateIfConstainsOnlyInternalAccepts(){
		Class<?> type = InternalAcceptsInterceptor.class;
		List<Method> methods = stepInvoker.findAllMethods(type);
		validationRule.validate(type, methods);
	}

	@Test
	public void shouldValidateIfConstainsOnlyCustomAccepts(){
		Class<?> type = CustomAcceptsInterceptor.class;
		List<Method> methods = stepInvoker.findAllMethods(type);
		validationRule.validate(type, methods);
	}

	@AcceptsWithAnnotations(NotLogged.class)
	public class InternalAndCustomAcceptsInterceptor {
		@Accepts public boolean accepts(){ return true; }
	}

	public class InternalAcceptsInterceptor {
		@Accepts public boolean accepts(){ return true; }
	}

	@AcceptsWithAnnotations(NotLogged.class)
	public class CustomAcceptsInterceptor {
	}
}
