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
package br.com.caelum.vraptor.view;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import javax.enterprise.inject.Vetoed;
import javax.servlet.ServletContext;

import br.com.caelum.vraptor.core.ReflectionProvider;
import br.com.caelum.vraptor.http.route.Router;

@Vetoed
public class Linker {

	private final Router router;
	private final ServletContext context;
	private final List<Object> args;
	private final String methodName;
	private final Class<?> controller;
	private final ReflectionProvider reflectionProvider;

	public Linker(ServletContext context, Router router, Class<?> controller, String methodName, List<Object> args,
			ReflectionProvider reflectionProvider) {
		this.router = router;
		this.context = context;
		this.controller = controller;
		this.methodName = methodName;
		this.args = args;
		this.reflectionProvider = reflectionProvider;
	}

	protected String getLink() {
		Method method = getMethod();
		return getPrefix() + router.urlFor(controller, method, getArgs(method));
	}

	protected String getPrefix() {
		return context.getContextPath();
	}
	
	protected Method getMethod() {
		Method method = null;

		if (countMethodsWithSameName() > 1) {
			method = reflectionProvider.getMethod(controller, methodName, getClasses(args));
			if (method == null && args.isEmpty()) {
				throw new IllegalArgumentException("Ambiguous method '" + methodName + "' on " + controller + ". Try to add some parameters to resolve ambiguity, or use different method names.");
			}
		} else {
			method = findMethodWithName(controller, methodName);
		}

		if(method == null) {
			throw new IllegalArgumentException(
				String.format("There are no methods on %s named '%s' that receives args of types %s",
						controller, methodName, Arrays.toString(getClasses(args))));
		}
		return method;
	}

	protected Object[] getArgs(Method method) {
		int methodArity = method.getParameterTypes().length;

		if (args.size() == methodArity) {
			return args.toArray();
		}

		if (args.size() > methodArity) {
			throw new IllegalArgumentException(String.format("linkTo param args must have the same or lower length as method param args. linkTo args: %d | method args: %d", args.size(), methodArity));
		}

		Object[] noMissingParamsArgs = new Object[methodArity];
		System.arraycopy(args.toArray(), 0, noMissingParamsArgs, 0, args.size());

		return noMissingParamsArgs;
	}

	protected Method findMethodWithName(Class<?> type, String name) {
		for (Method method : type.getDeclaredMethods()) {
			if (!method.isBridge() && method.getName().equals(name)) {
				return method;
			}
		}

		if (type.getSuperclass().equals(Object.class)) {
			return null;
		}

		return findMethodWithName(type.getSuperclass(), name);
	}

	protected int countMethodsWithSameName() {
		int amount = 0;
		for (Method method : controller.getDeclaredMethods()) {
			if (!method.isBridge() && method.getName().equals(methodName)) {
				amount++;
			}
		}

		return amount;
	}

	protected Class<?>[] getClasses(List<Object> params) {
		Class<?>[] classes = new Class<?>[params.size()];
		for(int i = 0; i < params.size(); i ++) {
			classes[i] = params.get(i).getClass();
		}
		return classes;
	}
}
