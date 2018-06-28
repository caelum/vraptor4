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
package br.com.caelum.vraptor.events;

import java.lang.reflect.Type;

import javax.enterprise.inject.Vetoed;

import br.com.caelum.vraptor.controller.ControllerMethod;
import br.com.caelum.vraptor.core.MethodInfo;
import br.com.caelum.vraptor.observer.ExecuteMethod;

/**
 * Event fired by {@link ExecuteMethod}
 * when it has fully completed it's execution.
 *
 * @author Rodrigo Turini
 * @since 4.0
 */
@Vetoed
public class MethodExecuted {

	private final ControllerMethod controllerMethod;
	private final MethodInfo methodInfo;

	public MethodExecuted(ControllerMethod method, MethodInfo methodInfo) {
		this.controllerMethod = method;
		this.methodInfo = methodInfo;
	}

	public ControllerMethod getControllerMethod() {
		return controllerMethod;
	}

	public Type getMethodReturnType() {
		return getControllerMethod().getMethod().getGenericReturnType();
	}

	public MethodInfo getMethodInfo() {
		return methodInfo;
	}
}
