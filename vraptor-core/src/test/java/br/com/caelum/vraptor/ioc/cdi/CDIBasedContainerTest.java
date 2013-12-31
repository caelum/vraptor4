package br.com.caelum.vraptor.ioc.cdi;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.Callable;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;

import br.com.caelum.cdi.component.UsingCacheComponent;
import br.com.caelum.vraptor.WeldJunitRunner;
import br.com.caelum.vraptor.core.RequestInfo;
import br.com.caelum.vraptor.http.MutableRequest;
import br.com.caelum.vraptor.http.MutableResponse;
import br.com.caelum.vraptor.ioc.ContainerProvider;
import br.com.caelum.vraptor.ioc.GenericContainerTest;
import br.com.caelum.vraptor.ioc.WhatToDo;

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

	@Test
	public void shouldCreateComponentsWithCache(){
		UsingCacheComponent component = cdiBasedContainer.instanceFor(UsingCacheComponent.class);
		component.putWithLRU("test","test");
		component.putWithDefault("test2","test2");
		assertEquals(component.putWithLRU("test","test"),"test");
		assertEquals(component.putWithDefault("test2","test2"),"test2");
	}

}