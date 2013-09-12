package br.com.caelum.vraptor4.ioc.cdi;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.CDI;

import org.hamcrest.MatcherAssert;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import br.com.caelum.cdi.component.CDIControllerComponent;
import br.com.caelum.cdi.component.CDISessionComponent;
import br.com.caelum.vraptor4.core.BaseComponents;
import br.com.caelum.vraptor4.core.RequestInfo;
import br.com.caelum.vraptor4.interceptor.PackagesAcceptor;
import br.com.caelum.vraptor4.ioc.Container;
import br.com.caelum.vraptor4.ioc.ContainerProvider;
import br.com.caelum.vraptor4.ioc.GenericContainerTest;
import br.com.caelum.vraptor4.ioc.WhatToDo;
import br.com.caelum.vraptor4.ioc.fixture.ComponentFactoryInTheClasspath;
import br.com.caelum.vraptor4.ioc.fixture.CustomComponentWithLifecycleInTheClasspath;
import br.com.caelum.vraptor4.validator.MessageInterpolatorFactory;
import br.com.caelum.vraptor4.validator.ValidatorFactoryCreator;
import br.com.caelum.vraptor4.validator.ValidatorCreator;
import br.com.caelum.vraptor4.validator.ValidatorFactoryCreator;

public class CDIBasedContainerTest extends GenericContainerTest {

	private static CdiContainer cdiContainer;
	private final ServletContainerFactory servletContainerFactory = new ServletContainerFactory();
	private int counter;

	@BeforeClass
	public static void startCDIContainer(){
		cdiContainer = new br.com.caelum.vraptor4.ioc.cdi.CdiContainer();
		cdiContainer.start();
	}

	@AfterClass
	public static void shutdownCDIContainer() {
		cdiContainer.shutdown();
	}

	@Override
	protected ContainerProvider getProvider() {
		return CDI.current().select(CDIProvider.class).get();
	}

	@Override
	protected <T> T executeInsideRequest(final WhatToDo<T> execution) {
		Callable<T> task = new Callable<T>() {
			@Override
			public T call() throws Exception {
				cdiContainer.startRequest();
				cdiContainer.startSession();
				RequestInfo request = new RequestInfo(context, null,
						servletContainerFactory.getRequest(),
						servletContainerFactory.getResponse());

				T result = execution.execute(request, counter);

				cdiContainer.stopSession();
				cdiContainer.stopRequest();
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
			java.lang.reflect.Field field = instance.getClass()
					.getDeclaredField("BEAN_INSTANCE_CACHE");
			field.setAccessible(true);
			ThreadLocal mapa = (ThreadLocal) field.get(instance);
			return mapa.get();
		} catch (Exception exception) {
			return instance;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	protected <T> T instanceFor(final Class<T> component,
			Container container) {
		T maybeAWeldProxy = container.instanceFor(component);
		return (T)actualInstance(maybeAWeldProxy);
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
		PackagesAcceptor acceptor = getFromContainer(PackagesAcceptor.class);
		actualInstance(acceptor);
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

	@Test
	public void shouldUseComponentFactoryAsProducer() {
		ComponentToBeProduced component = getFromContainer(ComponentToBeProduced.class);
		initializeProxy(component);
		assertNotNull(component);
	}

	private void initializeProxy(Object component) {
		component.toString();
	}

	@Test
	public void shouldNotAddRequestScopeForComponentWithScope(){
		Bean<?> bean = cdiContainer.getBeanManager().getBeans(CDISessionComponent.class).iterator().next();
		assertTrue(bean.getScope().equals(SessionScoped.class));
	}

	@Test
	public void shouldStereotypeControllerWithRequestAndNamed(){
		Bean<?> bean = cdiContainer.getBeanManager().getBeans(CDIControllerComponent.class).iterator().next();
		assertTrue(bean.getScope().equals(RequestScoped.class));
	}

	@Override
	@Test
	public void canProvideAllApplicationScopedComponents() {
		Set<Class<?>> components = new HashSet<>(BaseComponents.getApplicationScoped().keySet());
		components.remove(ValidatorFactoryCreator.class);
		components.remove(ValidatorCreator.class);
		components.remove(MessageInterpolatorFactory.class);
		components.remove(ValidatorFactoryCreator.class);
		checkAvailabilityFor(true, components);
	}

	@Override
	protected void configureExpectations() {
    	Enumeration<String> emptyEnumeration = Collections.enumeration(Collections.<String>emptyList());
    	when(context.getInitParameterNames()).thenReturn(emptyEnumeration);
    	when(context.getAttributeNames()).thenReturn(emptyEnumeration);
	}

}