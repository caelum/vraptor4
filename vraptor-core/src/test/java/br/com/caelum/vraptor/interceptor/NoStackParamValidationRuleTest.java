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

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import br.com.caelum.vraptor.Accepts;
import br.com.caelum.vraptor.AroundCall;
import br.com.caelum.vraptor.BeforeCall;
import br.com.caelum.vraptor.Intercepts;
import br.com.caelum.vraptor.core.DefaultReflectionProvider;
import br.com.caelum.vraptor.core.InterceptorStack;


public class NoStackParamValidationRuleTest {

	@Rule
	public ExpectedException exception = ExpectedException.none();

	private NoStackParamValidationRule validationRule;
	private StepInvoker invoker;

	@Before
	public void setUp() {
		invoker = new StepInvoker(new DefaultReflectionProvider());
		validationRule = new NoStackParamValidationRule(invoker);
	}

	@Test
	public void mustReceiveStackAsParameterForAroundCall() {
		exception.expect(IllegalStateException.class);
		exception.expectMessage("@AroundCall method must receive br.com.caelum.vraptor.core.InterceptorStack or br.com.caelum.vraptor.interceptor.SimpleInterceptorStack");

		Class<?> type = AroundInterceptorWithoutSimpleStackParameter.class;
		validationRule.validate(type, invoker.findAllMethods(type));
	}

	@Test
	public void mustNotReceiveStackAsParameterForAcceptsCall() {
		exception.expect(IllegalStateException.class);
		exception.expectMessage("Non @AroundCall method must not receive br.com.caelum.vraptor.core.InterceptorStack or br.com.caelum.vraptor.interceptor.SimpleInterceptorStack");

		Class<?> type = AcceptsInterceptorWithStackAsParameter.class;
		validationRule.validate(type, invoker.findAllMethods(type));

	}

	@Test
	public void mustNotReceiveStackAsParameterForBeforeAfterCall() {
		exception.expect(IllegalStateException.class);
		exception.expectMessage("Non @AroundCall method must not receive br.com.caelum.vraptor.core.InterceptorStack or br.com.caelum.vraptor.interceptor.SimpleInterceptorStack");

		Class<?> type = BeforeAfterInterceptorWithStackAsParameter.class;
		validationRule.validate(type, invoker.findAllMethods(type));
	}

	@Intercepts
	class AroundInterceptorWithoutSimpleStackParameter {
		@AroundCall
		public void intercept() {}
	}

	@Intercepts
	public class AcceptsInterceptorWithStackAsParameter {
		@Accepts
		boolean accepts(SimpleInterceptorStack stack){
			return true;
		}
	}

	@Intercepts
	class BeforeAfterInterceptorWithStackAsParameter{
		@BeforeCall
		public void before(InterceptorStack interceptorStack) {}
	}
}
