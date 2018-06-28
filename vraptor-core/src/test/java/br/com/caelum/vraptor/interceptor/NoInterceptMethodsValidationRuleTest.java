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

import br.com.caelum.vraptor.AfterCall;
import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.Intercepts;
import br.com.caelum.vraptor.core.DefaultReflectionProvider;

public class NoInterceptMethodsValidationRuleTest {

	@Rule
	public ExpectedException exception = ExpectedException.none();

	private StepInvoker stepInvoker;

	@Intercepts
	class SimpleInterceptor {
		public void dummyMethodWithoutInterceptorAnnotations() {}
	}

	@Intercepts
	class SimpleInterceptorWithCallableMethod {
		@AfterCall public void afterCall() {}
	}

	@Before
	public void setUp() {
		this.stepInvoker = new StepInvoker(new DefaultReflectionProvider());
	}

	@Test
	public void shoulThrowExceptionIfInterceptorDontHaveAnyCallableMethod() {
		exception.expect(InterceptionException.class);
		exception.expectMessage("Interceptor " + SimpleInterceptor.class.getCanonicalName() +" must declare at least one method whith @AfterCall, @AroundCall or @BeforeCall annotation");
		
		Class<?> type = SimpleInterceptor.class;
		List<Method> allMethods = stepInvoker.findAllMethods(type);
		new NoInterceptMethodsValidationRule(stepInvoker).validate(type, allMethods);
	}

	@Test
	public void shoulWorksFineIfInterceptorHaveAtLeastOneCallableMethod() {
		Class<?> type = SimpleInterceptorWithCallableMethod.class;
		List<Method> allMethods = stepInvoker.findAllMethods(type);
		new NoInterceptMethodsValidationRule(stepInvoker).validate(type, allMethods);
	}
}
