/***
 * Copyright (c) 2009 Caelum - www.caelum.com.br/opensource
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package br.com.caelum.vraptor.core;

import java.util.LinkedList;

import org.junit.Before;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.com.caelum.vraptor.controller.ControllerMethod;
import static org.mockito.Mockito.when;

public class DefaultInterceptorStackTest {

	private static final ControllerMethod A_METHOD = null;
	private static final Object AN_INSTANCE = null;

	private @Mock InterceptorStackHandlersCache handlersCache;
	private @Mock(name = "first") InterceptorHandler firstHandler;
	private @Mock(name = "second") InterceptorHandler secondHandler;

	private DefaultInterceptorStack stack;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		
		stack = new DefaultInterceptorStack(handlersCache);
		
		LinkedList<InterceptorHandler> handlers = new LinkedList<>();
		handlers.add(firstHandler);
		handlers.add(secondHandler);
		when(handlersCache.getInterceptorHandlers()).thenReturn(handlers);
		
	}
}
