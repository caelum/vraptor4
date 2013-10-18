package br.com.caelum.vraptor.interceptor;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.controller.ControllerMethod;
import br.com.caelum.vraptor.core.MethodInfo;
import br.com.caelum.vraptor.http.Parameter;
import br.com.caelum.vraptor.http.ParameterNameProvider;
import br.com.caelum.vraptor.util.test.MockResult;

public class ParameterIncluderInterceptorTest {

	@Mock private MethodInfo info;
	@Mock private ParameterNameProvider nameProvider;
	@Mock private ControllerMethod controllerMethod;
	private Method anyControllerMethod;
	private Result result;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		result = new MockResult();
		this.anyControllerMethod = Object.class.getEnclosingMethod();
	}

	@Test
	public void shoulIncludeParameterToView() throws Exception {

		ParameterIncluderInterceptor returnParamInterceptor =
			new ParameterIncluderInterceptor(info, result, nameProvider, controllerMethod);

		when(info.getParameters()).thenReturn(new Object[]{"value"});
		when(controllerMethod.getMethod()).thenReturn(anyControllerMethod);
		
		Parameter[] parameters = new Parameter[] {new Parameter("key", 0, null)};
		when(nameProvider.parametersFor(anyControllerMethod)).thenReturn(parameters);

		returnParamInterceptor.intercept();

		assertThat("value", is(equalTo(result.included().get("key"))));
	}

}