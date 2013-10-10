package br.com.caelum.vraptor.view;

import static org.hamcrest.Matchers.is;

import static org.junit.Assert.assertThat;

import static org.mockito.Mockito.when;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;

import net.vidageek.mirror.dsl.Mirror;
import net.vidageek.mirror.exception.MirrorException;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.com.caelum.vraptor.controller.DefaultBeanClass;
import br.com.caelum.vraptor.http.route.Router;
import br.com.caelum.vraptor.proxy.JavassistProxifier;

public class LinkToHandlerTest {
	private @Mock ServletContext context;
	private @Mock Router router;
	private LinkToHandler handler;
	private Method method2params;
	private Method method1param;
	private Method anotherMethod;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		this.handler = new LinkToHandler(context, router, new JavassistProxifier());
		when(context.getContextPath()).thenReturn("/path");

		this.method2params = new Mirror().on(TestController.class).reflect().method("method").withArgs(String.class, int.class);
		this.method1param = new Mirror().on(TestController.class).reflect().method("method").withArgs(String.class);
		this.anotherMethod = new Mirror().on(TestController.class).reflect().method("anotherMethod").withArgs(String.class, int.class);
	}

	@Ignore("Does it worth?")
	@Test(expected = IllegalArgumentException.class)
	public void shouldThrowExceptionWhenInvocationIsIncomplete() {
		//${linkTo[TestController]}
		handler.get(new DefaultBeanClass(TestController.class)).toString();
	}

	@Ignore("The method won't exist")
	@Test(expected = IllegalArgumentException.class)
	public void shouldThrowExceptionWhenInvokingInexistantMethod() throws Throwable {
		//${linkTo[TestController].nonExists}
		invoke(handler.get(new DefaultBeanClass(TestController.class)), "nonExists");
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldThrowExceptionWhenMethodIsAmbiguous() throws Throwable {
		//${linkTo[TestController].method()}
		invoke(handler.get(new DefaultBeanClass(TestController.class)), "method");
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldThrowExceptionWhenUsingParametersOfWrongTypes() throws Throwable {
		//${linkTo[TestController].method(123)}
		invoke(handler.get(new DefaultBeanClass(TestController.class)), "method", 123);
	}


	@Ignore("shoud not work whitout passing the parameters")
	@Test
	public void shouldReturnWantedUrlWithoutArgs() throws Throwable {
		when(router.urlFor(TestController.class, anotherMethod, new Object[2])).thenReturn("/expectedURL");

		//${linkTo[TestController].anotherMethod()}
		String uri = invoke(handler.get(new DefaultBeanClass(TestController.class)), "anotherMethod");
		assertThat(uri, is("/path/expectedURL"));
	}

	@Ignore("shoud not work whitout passing the parameters")	
	@Test
	public void shouldReturnWantedUrlWithoutArgsUsingPropertyAccess() throws Throwable {
		when(router.urlFor(TestController.class, anotherMethod, new Object[2])).thenReturn("/expectedURL");

		//${linkTo[TestController].anotherMethod}
		String uri = invoke(handler.get(new DefaultBeanClass(TestController.class)), "getAnotherMethod");
		assertThat(uri, is("/path/expectedURL"));
	}

	@Test
	public void shouldReturnWantedUrlWithParamArgs() throws Throwable {
		String a = "test";
		int b = 3;
		when(router.urlFor(TestController.class, method2params, new Object[]{a, b})).thenReturn("/expectedURL");
		//${linkTo[TestController].method('test', 3)}
		String uri = invoke(handler.get(new DefaultBeanClass(TestController.class)), "method", a, b);
		assertThat(uri, is("/path/expectedURL"));
	}

	@Ignore("I don't think so...")
	@Test
	public void shouldReturnWantedUrlWithPartialParamArgs() throws Throwable {
		String a = "test";
		when(router.urlFor(TestController.class, anotherMethod, new Object[]{a, null})).thenReturn("/expectedUrl");
		//${linkTo[TestController].anotherMethod('test')}
		String uri = invoke(handler.get(new DefaultBeanClass(TestController.class)), "anotherMethod", a);
		assertThat(uri, is("/path/expectedUrl"));
	}

	@Test
	public void shouldReturnWantedUrlForOverrideMethodWithParamArgs() throws Throwable {
		String a = "test";
		when(router.urlFor(SubGenericController.class, SubGenericController.class.getDeclaredMethod("method", new Class[]{String.class}), new Object[]{a})).thenReturn("/expectedURL");
		//${linkTo[TestSubGenericController].method('test')}]
		String uri = invoke(handler.get(new DefaultBeanClass(SubGenericController.class)), "method", a);
		assertThat(uri, is("/path/expectedURL"));
	}

	@Ignore("I don't think so...")
	@Test
	public void shouldReturnWantedUrlForOverrideMethodWithParialParamArgs() throws Throwable {
		String a = "test";
		when(router.urlFor(SubGenericController.class, SubGenericController.class.getDeclaredMethod("anotherMethod", new Class[]{String.class, String.class}), new Object[]{a, null})).thenReturn("/expectedURL");
		//${linkTo[TestSubGenericController].anotherMethod('test')}]
		String uri = invoke(handler.get(new DefaultBeanClass(SubGenericController.class)), "anotherMethod", a);
		assertThat(uri, is("/path/expectedURL"));
	}

	@Test
	public void shouldUseExactlyMatchedMethodIfTheMethodIsOverloaded() throws Throwable {
		String a = "test";
		when(router.urlFor(TestController.class, method1param, a)).thenReturn("/expectedUrl");
		//${linkTo[TestController].method('test')}
		String uri = invoke(handler.get(new DefaultBeanClass(TestController.class)), "method", a);
		assertThat(uri, is("/path/expectedUrl"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldThrowExceptionWhenPassingMoreArgsThanMethodSupports() throws Throwable {
		String a = "test";
		int b = 3;
		String c = "anotherTest";
		//${linkTo[TestController].anotherMethod('test', 3, 'anotherTest')}
		invoke(handler.get(new DefaultBeanClass(TestController.class)), "anotherMethod", a, b, c);
	}

	private String invoke(Object obj, String methodName, Object...args) throws Throwable {
		
		Class<?>[] clazzes = extractClass(args);
		
		try {
			Method method = new Mirror().on(obj.getClass()).reflect().method(methodName).withArgs(clazzes);
			if (methodName.startsWith("get")) {
				return method.invoke(obj).toString();
			}
			return method.invoke(obj, args).toString();
		} catch (MirrorException | InvocationTargetException e) {
			throw e.getCause() == null? e : e.getCause();
		}
	}

	private Class<?>[] extractClass(Object... args) {
		List<Class<?>> classes = new ArrayList<>();
		
		for(Object o: args){
			classes.add(o.getClass());
		}
		return classes.toArray(new Class<?>[0]);
	}

	static class TestController {
		void method(String a, int b) {

		}
		void method(String a) {

		}
		void anotherMethod(String a, int b) {

		}
	}
}
