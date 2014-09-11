/***
 * Copyright (c) 2009 Caelum - www.caelum.com.br/opensource All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package br.com.caelum.vraptor.ioc.cdi;

import static org.junit.Assert.assertEquals;

import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;

import br.com.caelum.cdi.component.UsingCacheComponent;
import br.com.caelum.vraptor.WeldJunitRunner;
import br.com.caelum.vraptor.events.VRaptorInitialized;
import br.com.caelum.vraptor.ioc.GenericContainerTest;

@RunWith(WeldJunitRunner.class)
public class CDIBasedContainerTest extends GenericContainerTest {

	@Inject private CDIBasedContainer cdiBasedContainer;
	@Inject private Event<VRaptorInitialized> initEvent;
	@Inject private Contexts contexts;

	@Override
	public void setup() throws Exception {
		contexts.startApplicationScope();
		contexts.startSessionScope();
		initEvent.fire(new VRaptorInitialized(null));
		super.setup();
	}
	
	@After
	public void tearDown() {
		contexts.stopSessionScope();
		contexts.stopApplicationScope();
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