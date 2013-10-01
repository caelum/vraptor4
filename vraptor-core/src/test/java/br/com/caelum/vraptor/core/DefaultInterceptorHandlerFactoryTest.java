package br.com.caelum.vraptor.core;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import br.com.caelum.vraptor.cache.VRaptorCache;
import br.com.caelum.vraptor.cache.VRaptorDefaultCache;
import br.com.caelum.vraptor.factory.Factories;
import br.com.caelum.vraptor.interceptor.AspectStyleInterceptorHandler;
import br.com.caelum.vraptor.interceptor.Interceptor;
import br.com.caelum.vraptor.interceptor.InterceptorMethodParametersResolver;
import br.com.caelum.vraptor.interceptor.StepInvoker;
import br.com.caelum.vraptor.ioc.Container;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;

import static org.junit.Assert.assertThat;

public class DefaultInterceptorHandlerFactoryTest {

	private Container container;

	private DefaultInterceptorHandlerFactory factory;
	
	private StepInvoker stepInvoker = Factories.createStepInvoker();

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		
		InterceptorMethodParametersResolver parametersResolver = new InterceptorMethodParametersResolver(container);
		
		VRaptorCache<Class<?>, InterceptorHandler> cachedHandlers = new VRaptorDefaultCache<>();
		factory = new DefaultInterceptorHandlerFactory(container, stepInvoker, parametersResolver, cachedHandlers);
	}

	static interface RegularInterceptor extends Interceptor {}

	@Test
	public void handlerForRegularInterceptorsShouldBeDynamic() throws Exception {
		assertThat(factory.handlerFor(RegularInterceptor.class), is(instanceOf(ToInstantiateInterceptorHandler.class)));
	}

	@Test
	public void handlerForAspectStyleInterceptorsShouldBeDynamic() throws Exception {
		assertThat(factory.handlerFor(AspectStyleInterceptor.class), is(instanceOf(AspectStyleInterceptorHandler.class)));
	}

	@Test
	public void aspectStyleHandlersShouldBeCached() throws Exception {
		InterceptorHandler handler = factory.handlerFor(AspectStyleInterceptor.class);
		assertThat(factory.handlerFor(AspectStyleInterceptor.class), is(sameInstance(handler)));
	}
}
