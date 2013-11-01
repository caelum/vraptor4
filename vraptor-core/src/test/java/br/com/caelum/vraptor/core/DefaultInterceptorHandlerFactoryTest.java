package br.com.caelum.vraptor.core;

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.com.caelum.vraptor.cache.CacheStore;
import br.com.caelum.vraptor.cache.DefaultCacheStore;
import br.com.caelum.vraptor.factory.Factories;
import br.com.caelum.vraptor.interceptor.AspectStyleInterceptorHandler;
import br.com.caelum.vraptor.interceptor.CustomAcceptsExecutor;
import br.com.caelum.vraptor.interceptor.Interceptor;
import br.com.caelum.vraptor.interceptor.InterceptorAcceptsExecutor;
import br.com.caelum.vraptor.interceptor.InterceptorExecutor;
import br.com.caelum.vraptor.interceptor.SimpleInterceptorStack;
import br.com.caelum.vraptor.interceptor.StepInvoker;
import br.com.caelum.vraptor.ioc.Container;

public class DefaultInterceptorHandlerFactoryTest {

	private Container container;

	private DefaultInterceptorHandlerFactory factory;

	private StepInvoker stepInvoker = Factories.createStepInvoker();

	private @Mock SimpleInterceptorStack simpleStack;
	private @Mock InterceptorAcceptsExecutor acceptsExecutor;
	private @Mock CustomAcceptsExecutor customAcceptsExecutor;

	private InterceptorExecutor interceptorExecutor;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		CacheStore<Class<?>, InterceptorHandler> cachedHandlers = new DefaultCacheStore<>();
		factory = new DefaultInterceptorHandlerFactory(container, stepInvoker,
				cachedHandlers, acceptsExecutor, customAcceptsExecutor, interceptorExecutor);
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