package br.com.caelum.vraptor.ioc.cdi;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;

import java.lang.reflect.Field;
import java.util.concurrent.Callable;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.spi.Bean;
import javax.inject.Inject;

import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.junit.runner.RunWith;

import br.com.caelum.cdi.component.CDIControllerComponent;
import br.com.caelum.cdi.component.UsingCacheComponent;
import br.com.caelum.vraptor.WeldJunitRunner;
import br.com.caelum.vraptor.core.RequestInfo;
import br.com.caelum.vraptor.http.MutableRequest;
import br.com.caelum.vraptor.http.MutableResponse;
import br.com.caelum.vraptor.interceptor.PackagesAcceptor;
import br.com.caelum.vraptor.ioc.Container;
import br.com.caelum.vraptor.ioc.ContainerProvider;
import br.com.caelum.vraptor.ioc.GenericContainerTest;
import br.com.caelum.vraptor.ioc.WhatToDo;
import br.com.caelum.vraptor.ioc.fixture.ComponentFactoryInTheClasspath;
import br.com.caelum.vraptor.ioc.fixture.CustomComponentWithLifecycleInTheClasspath;

@RunWith(WeldJunitRunner.class)
public class CDIBasedContainerTest extends GenericContainerTest {

	@Inject private CDIBasedContainer cdiBasedContainer;
	@Inject private CDIProvider cdiProvider;
	@Inject private Contexts contexts;

	private int counter;

	@Override
	protected ContainerProvider getProvider() {
		return cdiProvider;
	}

	@Override
	public void tearDown() {
		super.tearDown();
		contexts.stopRequestScope();
		contexts.stopConversationScope();
		contexts.stopSessionScope();
		contexts.stopApplicationScope();
	}

	@Override
	protected <T> T executeInsideRequest(final WhatToDo<T> execution) {
		Callable<T> task = new Callable<T>() {
			@Override
			public T call() throws Exception {
				contexts.startRequestScope();
				contexts.startSessionScope();

				RequestInfo request = new RequestInfo(null, null,
						cdiBasedContainer.instanceFor(MutableRequest.class),
						cdiBasedContainer.instanceFor(MutableResponse.class));

				T result = execution.execute(request, counter);

				contexts.stopRequestScope();
				contexts.stopSessionScope();

				return result;
			}
		};
		try {
			T call = task.call();
			return call;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings("rawtypes")
	private Object actualInstance(Object instance) {
		try {
			//sorry, but i have to initialize the weld proxy
			initializeProxy(instance);
			Field field = instance.getClass().getDeclaredField("BEAN_INSTANCE_CACHE");
			field.setAccessible(true);
			ThreadLocal mapa = (ThreadLocal) field.get(instance);
			return mapa.get();
		} catch (Exception exception) {
			return instance;
		}
	}

	@Override
	protected <T> T instanceFor(final Class<T> component, Container container) {
		T maybeAWeldProxy = container.instanceFor(component);
		return component.cast(actualInstance(maybeAWeldProxy));
	}

	@Override
	protected void checkSimilarity(Class<?> component, boolean shouldBeTheSame,
			Object firstInstance, Object secondInstance) {
		if (shouldBeTheSame) {
			MatcherAssert.assertThat("Should be the same instance for "
					+ component.getName(), actualInstance(firstInstance),
					is(equalTo(actualInstance(secondInstance))));
		} else {
			MatcherAssert.assertThat("Should not be the same instance for "
					+ component.getName(), actualInstance(firstInstance),
					is(not(equalTo(actualInstance(secondInstance)))));
		}
	}

	@Test
	public void instantiateCustomAcceptor(){
		actualInstance(cdiBasedContainer.instanceFor(PackagesAcceptor.class));
	}

	@Override
	@Test
	public void callsPredestroyExactlyOneTime() throws Exception {
		MyAppComponentWithLifecycle component = getFromContainer(MyAppComponentWithLifecycle.class);
		assertThat(component.getCalls(), is(0));
		shutdownCDIContainer();
		assertThat(component.getCalls(), is(1));
		startCDIContainer();
	}

	@Override
	@Test
	public void shoudCallPredestroyExactlyOneTimeForComponentsScannedFromTheClasspath() {
		CustomComponentWithLifecycleInTheClasspath component = getFromContainer(CustomComponentWithLifecycleInTheClasspath.class);
		assertThat(component.getCallsToPreDestroy(), is(equalTo(0)));
		shutdownCDIContainer();
		assertThat(component.getCallsToPreDestroy(), is(equalTo(1)));
		startCDIContainer();
	}

	@Override
	@Test
	public void shoudCallPredestroyExactlyOneTimeForComponentFactoriesScannedFromTheClasspath() {
		ComponentFactoryInTheClasspath componentFactory = getFromContainer(ComponentFactoryInTheClasspath.class);
		assertThat(componentFactory.getCallsToPreDestroy(), is(equalTo(0)));
		shutdownCDIContainer();
		assertThat(componentFactory.getCallsToPreDestroy(), is(equalTo(1)));
		startCDIContainer();
	}

	private void initializeProxy(Object component) {
		component.toString();
	}

	@Test
	public void shouldStereotypeControllerWithRequestAndNamed(){
		Bean<?> bean = cdiContainer.getBeanManager().getBeans(CDIControllerComponent.class).iterator().next();
		assertTrue(bean.getScope().equals(RequestScoped.class));
	}

	@Test
	public void shouldCreateComponentsWithCache(){
		UsingCacheComponent component = cdiBasedContainer.instanceFor(UsingCacheComponent.class);
		component.putWithLRU("test","test");
		component.putWithDefault("test2","test2");
		assertEquals(component.putWithLRU("test","test"),"test");
		assertEquals(component.putWithDefault("test2","test2"),"test2");
	}

}