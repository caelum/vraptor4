package br.com.caelum.vraptor.validator;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.AccessibleObject;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.cache.DefaultCacheStore;
import br.com.caelum.vraptor.controller.ControllerMethod;
import br.com.caelum.vraptor.core.MethodInfo;
import br.com.caelum.vraptor.http.Parameter;
import br.com.caelum.vraptor.http.ParameterNameProvider;
import br.com.caelum.vraptor.http.ParanamerNameProvider;

public class ReplicatorOutjectorTest {

	private ParameterNameProvider provider;
	private @Mock MethodInfo methodInfo;
	private @Mock Result result;
	private @Mock ControllerMethod controllerMethod;

	private Outjector outjector;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		when(controllerMethod.getMethod()).thenReturn(getClass().getDeclaredMethod("foo", int.class, float.class, long.class));
		when(methodInfo.getControllerMethod()).thenReturn(controllerMethod);

		provider = new ParanamerNameProvider(new DefaultCacheStore<AccessibleObject, Parameter[]>());
		outjector = new ReplicatorOutjector(result, methodInfo, provider);
	}

	@Test
	public void shouldReplicateMethodParametersToNextRequest() throws Exception {
		when(methodInfo.getParameters()).thenReturn(new Object[] {1, 2.0, 3l});

		outjector.outjectRequestMap();

		verify(result).include("first", 1);
		verify(result).include("second", 2.0);
		verify(result).include("third", 3l);
	}
	
	void foo(int first, float second, long third) { }
}
