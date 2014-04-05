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

import javax.enterprise.inject.Vetoed;

import net.vidageek.mirror.list.dsl.MirrorList;

import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.Accepts;
import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.Intercepts;

public class AcceptsNeedReturnBooleanValidationRuleTest {

	private AcceptsNeedReturnBooleanValidationRule validationRule;
	private StepInvoker stepInvoker;

	@Before
	public void setUp() {
		stepInvoker = new StepInvoker();
		validationRule = new AcceptsNeedReturnBooleanValidationRule(stepInvoker);
	}

	@Test(expected = InterceptionException.class)
	public void shouldVerifyIfAcceptsMethodReturnsVoid() {
		Class<VoidAcceptsInterceptor> type = VoidAcceptsInterceptor.class;
		MirrorList<Method> allMethods = stepInvoker.findAllMethods(type);
		validationRule.validate(type, allMethods);
	}

	@Test(expected = InterceptionException.class)
	public void shouldVerifyIfAcceptsMethodReturnsNonBooleanType() {
		Class<NonBooleanAcceptsInterceptor> type = NonBooleanAcceptsInterceptor.class;
		MirrorList<Method> allMethods = stepInvoker.findAllMethods(type);
		validationRule.validate(type, allMethods);
	}

	@Intercepts @Vetoed
	static class VoidAcceptsInterceptor {
		@Accepts public void accepts(){}
	}

	@Intercepts @Vetoed
	static class NonBooleanAcceptsInterceptor{
		@Accepts public String accepts() { return ""; }
	}
}