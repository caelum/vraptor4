package br.com.caelum.vraptor.secutiry;

import static br.com.caelum.vraptor.controller.DefaultControllerMethod.instanceFor;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;

import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import br.com.caelum.vraptor.*;
import br.com.caelum.vraptor.controller.ControllerMethod;
import br.com.caelum.vraptor.events.MethodReady;

@RunWith(MockitoJUnitRunner.class)
public class CsrfObserverTest {

	private CsrfObserver csrfObserver;
	@Mock private MethodReady event;

	@Before
	public void setUp() {
		csrfObserver = new CsrfObserver();
	}

	@Test
	public void should_ignore_requests_for_any_method_but_post() {
		when(event.getControllerMethod()).thenReturn(controllerMethod("get"));
		assertFalse(csrfObserver.needCsrfProtection(event));
		when(event.getControllerMethod()).thenReturn(controllerMethod("put"));
		assertFalse(csrfObserver.needCsrfProtection(event));
		when(event.getControllerMethod()).thenReturn(controllerMethod("delete"));
		assertFalse(csrfObserver.needCsrfProtection(event));
		when(event.getControllerMethod()).thenReturn(controllerMethod("post"));
		assertTrue(csrfObserver.needCsrfProtection(event));
	}
	
	private ControllerMethod controllerMethod(String name) {
		try {
			Method method = DummyController.class.getMethod(name);
			return instanceFor(DummyController.class, method);
		} catch (Exception e) {
			Assert.fail();
			throw new RuntimeException("couldn't get method " + name);
		}
	}

	@Controller
	static class DummyController {
		@Get public void get() {}
		@Put public void put() {}
		@Delete public void delete() {}
		@Post public void post() {}
	}
}