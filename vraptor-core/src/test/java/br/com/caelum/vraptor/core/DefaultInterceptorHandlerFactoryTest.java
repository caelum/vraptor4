package br.com.caelum.vraptor.core;

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;

import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.Intercepts;
import br.com.caelum.vraptor.Lazy;
import br.com.caelum.vraptor.interceptor.Interceptor;
import br.com.caelum.vraptor.ioc.Container;
import br.com.caelum.vraptor4.interceptor.AspectStyleInterceptorHandler;

public class DefaultInterceptorHandlerFactoryTest {

	private Container container;

	private DefaultInterceptorHandlerFactory factory;

	@Before
	public void setUp() throws Exception {
		factory = new DefaultInterceptorHandlerFactory(container);
	}

	static interface RegularInterceptor extends Interceptor {}
	@Lazy
	static interface ALazyInterceptor extends Interceptor {}
	
	@Intercepts
	static class AspectStyleInterceptor{}

	@Test
	public void handlerForRegularInterceptorsShouldBeDynamic() throws Exception {
		assertThat(factory.handlerFor(RegularInterceptor.class), is(instanceOf(ToInstantiateInterceptorHandler.class)));
	}
	
	@Test
	public void handlerForAspectStyleInterceptorsShouldBeDynamic() throws Exception {
		assertThat(factory.handlerFor(AspectStyleInterceptor.class), is(instanceOf(AspectStyleInterceptorHandler.class)));
	}	
	
	@Test
	public void handlerForStaticInterceptorsShouldBeStatic() throws Exception {
		assertThat(factory.handlerFor(ALazyInterceptor.class), is(instanceOf(LazyInterceptorHandler.class)));
	}
	
	@Test
	public void staticHandlersShouldBeCached() throws Exception {
		InterceptorHandler handler = factory.handlerFor(ALazyInterceptor.class);
		assertThat(factory.handlerFor(ALazyInterceptor.class), is(sameInstance(handler)));
	}
	
	@Test
	public void aspectStyleHandlersShouldBeCached() throws Exception {
		InterceptorHandler handler = factory.handlerFor(AspectStyleInterceptor.class);
		assertThat(factory.handlerFor(AspectStyleInterceptor.class), is(sameInstance(handler)));
	}
}
