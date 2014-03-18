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
import br.com.caelum.vraptor.events.EndOfInterceptorStack;
import br.com.caelum.vraptor.events.StackStarting;
import br.com.caelum.vraptor.util.test.MockInstanceImpl;

public class DefaultInterceptorStackTest {

	private ControllerInstance controllerInstance;
	
	private @Mock Object controller;
	private @Mock InterceptorStackHandlersCache cache;
	private @Mock ControllerMethod controllerMethod;
	private @Mock Event<EndOfInterceptorStack> event;
	private @Mock Event<StackStarting> stackStartingEvent;
	private @Mock InterceptorHandler handler;
	
	private DefaultInterceptorStack stack;
	

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		controllerInstance = new DefaultControllerInstance(controller);
		stack = new DefaultInterceptorStack(cache, new MockInstanceImpl<>(controllerMethod), new MockInstanceImpl<>(controllerInstance), event, stackStartingEvent);
		LinkedList<InterceptorHandler> handlers = new LinkedList<>();
		handlers.add(handler);
		
		when(cache.getInterceptorHandlers()).thenReturn(handlers);
	}
	
	@Test
	public void firesStartEventOnStart() throws Exception {
		stack.start();
		
		verify(stackStartingEvent).fire(any(StackStarting.class));
	}
	
	@Test
	public void executesTheFirstHandler() throws Exception {
		stack.start();
		
		verify(handler).execute(stack, controllerMethod, controller);
	}
	
	@Test
	public void doesntFireEndOfStackIfTheInterceptorsDontContinueTheStack() throws Exception {	
		stack.start();
		
		verify(event, never()).fire(any(EndOfInterceptorStack.class));
	}
	
	@Test
	public void firesEndOfStackIfAllInterceptorsWereExecuted() throws Exception {
		doAnswer(callNext()).when(handler).execute(stack, controllerMethod, controller);
		
		stack.start();
		
		verify(event).fire(any(EndOfInterceptorStack.class));
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
