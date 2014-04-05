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
package br.com.caelum.vraptor.core;

import static org.mockito.Mockito.*;

import java.util.LinkedList;

import javax.enterprise.event.Event;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import br.com.caelum.vraptor.controller.ControllerInstance;
import br.com.caelum.vraptor.controller.ControllerMethod;
import br.com.caelum.vraptor.controller.DefaultControllerInstance;
import br.com.caelum.vraptor.events.InterceptorsExecuted;
import br.com.caelum.vraptor.events.InterceptorsReady;
import br.com.caelum.vraptor.util.test.MockInstanceImpl;

public class DefaultInterceptorStackTest {

	private ControllerInstance controllerInstance;
	
	private @Mock Object controller;
	private @Mock InterceptorStackHandlersCache cache;
	private @Mock ControllerMethod controllerMethod;
	private @Mock Event<InterceptorsReady> interceptorsReadyEvent;
	private @Mock Event<InterceptorsExecuted> interceptorsExecutedEvent;
	private @Mock InterceptorHandler handler;
	
	private DefaultInterceptorStack stack;
	

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		controllerInstance = new DefaultControllerInstance(controller);
		stack = new DefaultInterceptorStack(cache, new MockInstanceImpl<>(controllerMethod), new MockInstanceImpl<>(controllerInstance), interceptorsExecutedEvent, interceptorsReadyEvent);
		LinkedList<InterceptorHandler> handlers = new LinkedList<>();
		handlers.add(handler);
		
		when(cache.getInterceptorHandlers()).thenReturn(handlers);
	}
	
	@Test
	public void firesStartEventOnStart() throws Exception {
		stack.start();
		
		verify(interceptorsReadyEvent).fire(any(InterceptorsReady.class));
	}
	
	@Test
	public void executesTheFirstHandler() throws Exception {
		stack.start();
		
		verify(handler).execute(stack, controllerMethod, controller);
	}
	
	@Test
	public void doesntFireEndOfStackIfTheInterceptorsDontContinueTheStack() throws Exception {	
		stack.start();
		
		verify(interceptorsExecutedEvent, never()).fire(any(InterceptorsExecuted.class));
	}
	
	@Test
	public void firesEndOfStackIfAllInterceptorsWereExecuted() throws Exception {
		doAnswer(callNext()).when(handler).execute(stack, controllerMethod, controller);
		
		stack.start();
		
		verify(interceptorsExecutedEvent).fire(any(InterceptorsExecuted.class));
	}

	private Answer<Void> callNext() {
		return new Answer<Void>() {

			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				stack.next(controllerMethod, controller);
				return null;
			}
		};
	}
}
