package br.com.caelum.vraptor.ioc.cdi;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.Callable;

import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;

import br.com.caelum.cdi.component.UsingCacheComponent;
import br.com.caelum.vraptor.WeldJunitRunner;
import br.com.caelum.vraptor.events.VRaptorInitialized;
import br.com.caelum.vraptor.ioc.GenericContainerTest;
import br.com.caelum.vraptor.ioc.WhatToDo;

@RunWith(WeldJunitRunner.class)
public class CDIBasedContainerTest extends GenericContainerTest {

	@Inject private CDIBasedContainer cdiBasedContainer;
	@Inject private Event<VRaptorInitialized> initEvent;
	@Inject private Contexts contexts;

	private int counter;
	
	@Override
	public void setup() throws Exception {
		initEvent.fire(new VRaptorInitialized(null));
		super.setup();
	}
	
	@After
	public void tearDown() {
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

				T result = execution.execute(counter);

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