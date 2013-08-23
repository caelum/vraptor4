/***
 * Copyright (c) 2009 Caelum - www.caelum.com.br/opensource - guilherme.silveira@caelum.com.br
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

package br.com.caelum.vraptor4.restfulie.controller;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

import javax.enterprise.context.RequestScoped;

import br.com.caelum.vraptor4.InterceptionException;
import br.com.caelum.vraptor4.controller.ControllerMethod;
import br.com.caelum.vraptor4.controller.HttpMethod;
import br.com.caelum.vraptor4.core.InterceptorStack;
import br.com.caelum.vraptor4.core.RequestInfo;
import br.com.caelum.vraptor4.core.Routes;
import br.com.caelum.vraptor4.interceptor.Interceptor;
import br.com.caelum.vraptor4.restfulie.Restfulie;
import br.com.caelum.vraptor4.restfulie.hypermedia.HypermediaResource;
import br.com.caelum.vraptor4.restfulie.hypermedia.Transition;
import br.com.caelum.vraptor4.restfulie.relation.Relation;
import br.com.caelum.vraptor4.restfulie.relation.RelationBuilder;
import br.com.caelum.vraptor4.view.Status;

/**
 * Intercepts invocations to state control's intercepted controllers.
 *
 * @author guilherme silveira
 * @author pedro mariano
 */
@RequestScoped
public class ControllerControlInterceptor<T extends HypermediaResource> implements Interceptor {

	private final ControllerControl<T> control;
	private final List<Class<?>> controllers;
	private final Status status;
	private final Restfulie restfulie;
	private final Routes routes;
	private final RequestInfo info;
	private final ParameterizedTypeSearcher searcher = new ParameterizedTypeSearcher();

	public ControllerControlInterceptor(ControllerControl<T> control, Restfulie restfulie, Status status, RequestInfo info, Routes routes) {
		this.control = control;
		this.restfulie = restfulie;
		this.status = status;
		this.info = info;
		this.routes = routes;
		this.controllers = Arrays.asList(control.getControllers());
	}

	public boolean accepts(ControllerMethod method) {
		return controllers.contains(method.getController().getType()) && method.getMethod().isAnnotationPresent(Transition.class);
	}

	public void intercept(InterceptorStack stack, ControllerMethod method,
			Object instance) throws InterceptionException {
		ParameterizedType type = searcher.search(control.getClass());
		if(analyzeImplementation(method,type)) {
			stack.next(method, instance);
		}
	}

	private boolean analyzeImplementation(ControllerMethod method,
			ParameterizedType parameterized) {
		Type parameterType = parameterized.getActualTypeArguments()[0];
		Class<?> found = (Class<?>) parameterType;
		T resource = retrieveResource(found);
		if(resource==null) {
			status.notFound();
			return false;
		}
		if(allows(resource, method.getMethod())) {
			return true;
		}
		status.methodNotAllowed(allowedMethods());
		return false;
	}

	private EnumSet<HttpMethod> allowedMethods() {
		EnumSet<HttpMethod> allowed = routes.allowedMethodsFor(info.getRequestedUri());
		allowed.remove(HttpMethod.of(info.getRequest()));
		return allowed;
	}


	private T retrieveResource(Class<?> found) {
		String parameterName = lowerFirstChar(found.getSimpleName()) + ".id";
		String id = info.getRequest().getParameter(parameterName);
		T resource = control.retrieve(id);
		return resource;
	}

	private boolean allows(T resource, Method method) {
		RelationBuilder builder = restfulie.newRelationBuilder();
		resource.configureRelations(builder);

		for (Relation relation : builder.getRelations()) {
			if(relation.matches(method)) {
				return true;
			}
		}
		return false;
	}

	private String lowerFirstChar(String simpleName) {
		if(simpleName.length()==1) {
			return simpleName.toLowerCase();
		}
		return Character.toLowerCase(simpleName.charAt(0)) + simpleName.substring(1);
	}

}
