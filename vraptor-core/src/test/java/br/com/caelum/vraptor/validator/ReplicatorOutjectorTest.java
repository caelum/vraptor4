package br.com.caelum.vraptor.validator;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
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

public class ReplicatorOutjectorTest {


	private @Mock ParameterNameProvider provider;
	private @Mock MethodInfo method;
	private @Mock Result result;
	private @Mock ControllerMethod controllerMethod;

	private Outjector outjector;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		when(method.getControllerMethod()).thenReturn(controllerMethod);
		outjector = new ReplicatorOutjector(result, method, provider);
	}

	@Test
	public void shouldReplicateMethodParametersToNextRequest() throws Exception {
		Parameter[] parameters = new Parameter[] { new Parameter("first", 0, null), 
				new Parameter("second", 1, null), new Parameter("third", 2, null)};
		when(provider.parametersFor(any(Method.class))).thenReturn(parameters);
		when(method.getParameters()).thenReturn(new Object[] {1, 2.0, 3l});

		outjector.outjectRequestMap();

		verify(result).include("first", 1);
		verify(result).include("second", 2.0);
		verify(result).include("third", 3l);
	}
}
