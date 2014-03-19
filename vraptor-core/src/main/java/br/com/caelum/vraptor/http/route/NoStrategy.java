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

package br.com.caelum.vraptor.http.route;

import java.lang.reflect.Method;
import java.util.EnumSet;

import javax.enterprise.inject.Vetoed;

import br.com.caelum.vraptor.controller.ControllerMethod;
import br.com.caelum.vraptor.controller.HttpMethod;
import br.com.caelum.vraptor.http.MutableRequest;

/**
 * A route strategy which is basically invalid in order to force users to not
 * forget to decide a route strategy.
 *
 * @author guilherme silveira
 */
@Vetoed
public class NoStrategy implements Route {

	@Override
	public ControllerMethod controllerMethod(MutableRequest request, String uri) {
		throw new IllegalRouteException("You have created a route, but did not specify any method to be invoked.");
	}

	@Override
	public String urlFor(Class<?> type, Method m, Object... params) {
		return "nothing";
	}

	@Override
	public boolean canHandle(Class<?> type, Method method) {
		return false;
	}

	@Override
	public boolean canHandle(String uri) {
		return false;
	}

	@Override
	public int getPriority() {
		return 0;
	}

	@Override
	public EnumSet<HttpMethod> allowedMethods() {
		return EnumSet.noneOf(HttpMethod.class);
	}

	@Override
	public String getOriginalUri() {
		return "NoStrategy";
	}
	
	@Override
	public ControllerMethod getControllerMethod() {
		return null;
	}

}
