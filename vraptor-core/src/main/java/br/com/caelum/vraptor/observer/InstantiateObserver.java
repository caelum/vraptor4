/***
 * Copyright (c) 2009 Caelum - www.caelum.com.br/opensource
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package br.com.caelum.vraptor.observer;

import static com.google.common.base.Preconditions.checkState;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import br.com.caelum.vraptor.controller.ControllerInstance;
import br.com.caelum.vraptor.controller.DefaultControllerInstance;
import br.com.caelum.vraptor.events.ControllerFound;
import br.com.caelum.vraptor.ioc.Container;

/**
 * Instantiates the current instance of controller class.
 *
 * @author Guilherme Silveira
 * @author Rodrigo Turini
 */
@RequestScoped
public class InstantiateObserver {

	private final Container container;
	private ControllerInstance controllerInstance;

	/**
	 * @deprecated CDI eyes only
	 */
	protected InstantiateObserver() {
		this(null);
	}

	@Inject
	public InstantiateObserver(Container container) {
		this.container = container;
	}

	public void instantiate(@Observes ControllerFound event) {
		Object instance = container.instanceFor(event.getController().getType());
		this.controllerInstance = new DefaultControllerInstance(instance);
	}

	@Produces @RequestScoped
	public ControllerInstance getControllerInstance() {
		checkState(controllerInstance != null, "ControllerInstance is not initialised yet");
		return this.controllerInstance;
	}
}