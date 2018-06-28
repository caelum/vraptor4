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
package br.com.caelum.vraptor.observer;

import java.lang.reflect.Method;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import br.com.caelum.vraptor.events.MethodReady;
import br.com.caelum.vraptor.interceptor.IncludeParameters;
import br.com.caelum.vraptor.validator.Outjector;

/**
 * Includes all the parameters on the view of a method
 * annotated with {@link IncludeParameters} annotation
 *
 * @author Rodrigo Turini
 * @since 4.0
 */
@ApplicationScoped
public class ParameterIncluder {

	private Instance<Outjector> outjector;

	/**
	 * @deprecated CDI eyes only
	 */
	protected ParameterIncluder() {
		this(null);
	}

	@Inject
	public ParameterIncluder(Instance<Outjector> outjector) {
		this.outjector = outjector;
	}

	public void include(@Observes MethodReady event) {
		Method method = event.getControllerMethod().getMethod();
		if (method.isAnnotationPresent(IncludeParameters.class)) {
			outjector.get().outjectRequestMap();
		}
	}
}
