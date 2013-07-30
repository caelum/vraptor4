package br.com.caelum.vraptor4.view;

import static br.com.caelum.vraptor4.view.Results.logic;
import static br.com.caelum.vraptor4.view.Results.page;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.com.caelum.vraptor4.Result;
import br.com.caelum.vraptor4.core.Localization;
import br.com.caelum.vraptor4.http.MutableRequest;
import br.com.caelum.vraptor4.http.ParametersProvider;
import br.com.caelum.vraptor4.http.route.ControllerNotFoundException;
import br.com.caelum.vraptor4.http.route.Router;
import br.com.caelum.vraptor4.restfulie.controller.ControllerMethod;
import br.com.caelum.vraptor4.restfulie.controller.DefaultControllerMethod;
import br.com.caelum.vraptor4.restfulie.controller.HttpMethod;
import br.com.caelum.vraptor4.view.DefaultRefererResult;
import br.com.caelum.vraptor4.view.LogicResult;
import br.com.caelum.vraptor4.view.PageResult;

public class DefaultRefererResultTest {

	private @Mock Result result;
	private @Mock MutableRequest request;
	private @Mock Router router;
	private @Mock Localization localization;
	private @Mock ParametersProvider provider;
	private DefaultRefererResult refererResult;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		refererResult = new DefaultRefererResult(result, request, router, provider, localization);
	}

	@Test
	public void whenThereIsNoRefererShouldThrowExceptionOnForward() throws Exception {
		when(request.getHeader("Referer")).thenReturn(null);
		
		try {
			refererResult.forward();
			fail("Expected IllegalStateException");
		} catch (IllegalStateException e) {
			
		}
	}
	
	@Test
	public void whenThereIsNoRefererShouldThrowExceptionOnRedirect() throws Exception {
		when(request.getHeader("Referer")).thenReturn(null);

		try {
			refererResult.redirect();
			fail("Expected IllegalStateException");
		} catch (IllegalStateException e) {
			
		}
	}
	
	@Test
	public void whenRefererDontMatchAControllerShouldForwardToPage() throws Exception {
		PageResult page = mock(PageResult.class);
		
		when(request.getHeader("Referer")).thenReturn("http://localhost:8080/vraptor/no-controller");
		when(request.getContextPath()).thenReturn("/vraptor");
		when(router.parse("/no-controller", HttpMethod.GET, request)).thenThrow(new ControllerNotFoundException());
		doReturn(page).when(result).use(page());

		refererResult.forward();
		
		verify(page).forwardTo("/no-controller");
	}
	
	@Test
	public void whenRefererDontMatchAControllerShouldRedirectToPage() throws Exception {
		PageResult page = mock(PageResult.class);
		
		when(request.getHeader("Referer")).thenReturn("http://localhost:8080/vraptor/no-controller");
		when(request.getContextPath()).thenReturn("/vraptor");
		when(router.parse("/no-controller", HttpMethod.GET, request)).thenThrow(new ControllerNotFoundException());
		doReturn(page).when(result).use(page());
		
		refererResult.redirect();
		
		verify(page).redirectTo("/no-controller");
	}
	
	public static class RefererController {
		public void index() {

		}
	}
	
	@Test
	public void whenRefererMatchesAControllerShouldRedirectToIt() throws Exception {
		LogicResult logic = mock(LogicResult.class);
		RefererController controller = mock(RefererController.class);

		Method index = RefererController.class.getMethod("index");
		ControllerMethod method = DefaultControllerMethod.instanceFor(RefererController.class, index);

		when(request.getHeader("Referer")).thenReturn("http://localhost:8080/vraptor/no-controller");
		when(request.getContextPath()).thenReturn("/vraptor");
		when(router.parse("/no-controller", HttpMethod.GET, request)).thenReturn(method);
		doReturn(logic).when(result).use(logic());
		when(logic.redirectTo(RefererController.class)).thenReturn(controller);

		refererResult.redirect();
		
		verify(logic).redirectTo(RefererController.class);
		verify(controller).index();
	}
	@Test
	public void whenRefererMatchesAControllerShouldForwardToIt() throws Exception {
		LogicResult logic = mock(LogicResult.class);
		RefererController controller = mock(RefererController.class);
		
		Method index = RefererController.class.getMethod("index");
		ControllerMethod method = DefaultControllerMethod.instanceFor(RefererController.class, index);
		
		when(request.getHeader("Referer")).thenReturn("http://localhost:8080/vraptor/no-controller");
		when(request.getContextPath()).thenReturn("/vraptor");
		when(router.parse("/no-controller", HttpMethod.GET, request)).thenReturn(method);
		doReturn(logic).when(result).use(logic());
		when(logic.forwardTo(RefererController.class)).thenReturn(controller);
		
		refererResult.forward();
		
		verify(logic).forwardTo(RefererController.class);
		verify(controller).index();
	}
}
