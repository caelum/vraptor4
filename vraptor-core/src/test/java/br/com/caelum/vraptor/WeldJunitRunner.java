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
package br.com.caelum.vraptor;

import javax.enterprise.inject.spi.CDI;

import org.jboss.weld.environment.se.Weld;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;

import br.com.caelum.vraptor.ioc.cdi.Contexts;

/**
 * Simple Junit class runner that initialize weld container
 */
public class WeldJunitRunner extends BlockJUnit4ClassRunner {

	private Class<?> clazz;
	
	public WeldJunitRunner(Class<?> testClass) throws InitializationError {
		super(testClass);
		new Weld().initialize();
		this.clazz = testClass;
	}

	/**
	 * With this, your test class is a CDI bean, so you can use DI
	 */
	@Override
	protected Object createTest() throws Exception {
		return CDI.current().select(clazz).get();
	}
	
	@Override
	protected void runChild(FrameworkMethod method, RunNotifier notifier) {
		Contexts contexts = CDI.current().select(Contexts.class).get();
		contexts.startRequestScope();
		super.runChild(method, notifier);
		contexts.stopRequestScope();
	}
}