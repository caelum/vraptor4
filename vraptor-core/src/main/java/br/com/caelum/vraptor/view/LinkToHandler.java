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
package br.com.caelum.vraptor.view;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewMethod;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.ServletContext;

import net.vidageek.mirror.dsl.Mirror;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor.controller.BeanClass;
import br.com.caelum.vraptor.http.route.Router;
import br.com.caelum.vraptor.proxy.MethodInvocation;
import br.com.caelum.vraptor.proxy.Proxifier;
import br.com.caelum.vraptor.proxy.SuperMethod;

import com.google.common.collect.ForwardingMap;

/**
 * View helper for generating uris
 * @author Lucas Cavalcanti
 * @since 3.4.0
 *
 */
@Named("linkTo")
@ApplicationScoped
public class LinkToHandler extends ForwardingMap<Class<?>, Object> {

	private static final Logger logger = LoggerFactory.getLogger(LinkToHandler.class);

	private ServletContext context;
	private Router router;

	private Proxifier proxifier;

	@Deprecated // CDI eyes only
	public LinkToHandler() {
	}

	@Inject
	public LinkToHandler(ServletContext context, Router router, Proxifier proxifier) {
		this.context = context;
		this.router = router;
		this.proxifier = proxifier;
	}

	@PostConstruct
	public void start() {
		logger.info("Registering linkTo component");
	}

	@Override
	protected Map<Class<?>, Object> delegate() {
		return Collections.emptyMap();
	}

	@Override
	public Object get(Object key) {
		BeanClass beanClass = (BeanClass) key;
		final Class<?> controller = beanClass.getType();
		String interfaceName = controller.getName() + "$linkTo";
		Class<?> linkToInterface;
		try {
			linkToInterface = Class.forName(interfaceName);
		} catch (ClassNotFoundException _) {
			linkToInterface = createLinkToInterface(controller, interfaceName);
		}
		return proxifier.proxify(linkToInterface, new MethodInvocation<Object>() {
			@Override
			public Object intercept(Object proxy, Method method, Object[] args, SuperMethod superMethod) {
				return new Linker(controller, method.getName(), Arrays.asList(args)).toString();
			}
		});
	}

	private Class<?> createLinkToInterface(final Class<?> controller, String interfaceName) {
		ClassPool pool = ClassPool.getDefault();
		CtClass inter = pool.makeInterface(interfaceName);
		try {
			for(String name : getMethodNames(new Mirror().on(controller).reflectAll().methods())) {
				CtMethod method = CtNewMethod.make(String.format("abstract String %s(Object[] args);", name), inter);
				method.setModifiers(method.getModifiers() | 128 /* Modifier.VARARGS */);
				inter.addMethod(method);
//				CtMethod getter = CtNewMethod.make(String.format("abstract String get%s();", StringUtils.capitalize(name)), inter);
//				inter.addMethod(getter);
			}
			return inter.toClass();
		} catch (CannotCompileException e) {
			throw new RuntimeException(e);
		}
	}

	private Set<String> getMethodNames(List<Method> methods) {
		Set<String> names = new HashSet<>();
		for (Method method : methods) {
			if (!method.getDeclaringClass().equals(Object.class)) {
				names.add(method.getName());
			}
		}
		return names;
	}

	class Linker extends ForwardingMap<Object, Linker> {

		private final List<Object> args;
		private final String methodName;
		private final Class<?> controller;

		public Linker(Class<?> controller, String methodName) {
			this(controller, methodName, new ArrayList<>());
		}

		public Linker(Class<?> controller, String methodName, List<Object> args) {
			this.controller = controller;
			this.methodName = methodName;
			this.args = args;
		}

		@Override
		public Linker get(Object key) {
			List<Object> newArgs = new ArrayList<>(args);
			newArgs.add(key);
			return new Linker(controller, methodName, newArgs);
		}

		@Override
		protected Map<Object, Linker> delegate() {
			return Collections.emptyMap();
		}

		@Override
		public String toString() {
			Method method = null;

			if (getMethodsAmountWithSameName() > 1) {
				method = new Mirror().on(controller).reflect().method(methodName).withArgs(getClasses(args));
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

			return context.getContextPath() + router.urlFor(controller, method, getArgs(method));
		}

		private Object[] getArgs(Method method) {
			int methodParamsQuantity = method.getParameterTypes().length;

			if (args.size() == methodParamsQuantity)
				return args.toArray();

			if (args.size() > methodParamsQuantity)
				throw new IllegalArgumentException(String.format("linkTo param args must have the same or lower length as method param args. linkTo args: %d | method args: %d", args.size(), methodParamsQuantity));

			Object[] noMissingParamsArgs = new Object[methodParamsQuantity];
			System.arraycopy(args.toArray(), 0, noMissingParamsArgs, 0, args.size());

			return noMissingParamsArgs;
		}

		private Method findMethodWithName(Class<?> type, String name) {
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

		private int getMethodsAmountWithSameName() {
			int amount = 0;
			for (Method method : controller.getDeclaredMethods()) {
				if (!method.isBridge() && method.getName().equals(methodName)) {
					amount++;
				}
			}

			return amount;
		}

		private Class<?>[] getClasses(List<Object> params) {
			Class<?>[] classes = new Class<?>[params.size()];
			for(int i = 0; i < params.size(); i ++) {
				classes[i] = params.get(i).getClass();
			}
			return classes;
	   }
	}
}
