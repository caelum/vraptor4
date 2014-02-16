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
import br.com.caelum.vraptor.factory.Factories;

public class ReplicatorOutjectorTest {

	private @Mock Result result;
	private MethodInfo methodInfo = new MethodInfo(Factories.createParameterNameProvider());
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
