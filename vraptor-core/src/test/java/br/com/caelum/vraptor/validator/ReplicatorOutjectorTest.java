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

import static org.mockito.Mockito.verify;

import java.lang.reflect.Method;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.controller.ControllerMethod;
import br.com.caelum.vraptor.controller.DefaultControllerMethod;
import br.com.caelum.vraptor.core.MethodInfo;
import br.com.caelum.vraptor.http.ParanamerNameProvider;

public class ReplicatorOutjectorTest {

	private @Mock Result result;
	private MethodInfo methodInfo = new MethodInfo(new ParanamerNameProvider());
	private Outjector outjector;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		Method method = getClass().getDeclaredMethod("foo", int.class, float.class, long.class);
		ControllerMethod controllerMethod = DefaultControllerMethod.instanceFor(getClass(), method);

		methodInfo.setControllerMethod(controllerMethod);

		outjector = new ReplicatorOutjector(result, methodInfo);
	}

	@Test
	public void shouldReplicateMethodParametersToNextRequest() throws Exception {
		methodInfo.setParameter(0, 1);
		methodInfo.setParameter(1, 2.0);
		methodInfo.setParameter(2, 3l);

		outjector.outjectRequestMap();

		verify(result).include("first", 1);
		verify(result).include("second", 2.0);
		verify(result).include("third", 3l);
	}

	void foo(int first, float second, long third) { }
}
