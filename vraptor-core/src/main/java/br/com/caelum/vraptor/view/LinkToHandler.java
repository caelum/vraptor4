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

import static br.com.caelum.vraptor.util.StringUtils.capitalize;
import static java.util.Arrays.fill;
import static java.util.Collections.sort;
import static javassist.CtNewMethod.abstractMethod;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
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
import br.com.caelum.vraptor.proxy.ProxyCreationException;
import br.com.caelum.vraptor.proxy.SuperMethod;
import br.com.caelum.vraptor.util.StringUtils;

import com.google.common.collect.ForwardingMap;

/**
 * View helper for generating uris
 * 
 * @author Otávio Garcia
 * @author Lucas Cavalcanti
 * @since 3.4.0
 */
@Named("linkTo")
@ApplicationScoped
public class LinkToHandler extends ForwardingMap<Class<?>, Object> {

	private static final Logger logger = LoggerFactory.getLogger(LinkToHandler.class);

	private final ServletContext context;
	private final Router router;
	private final Proxifier proxifier;

	private ConcurrentMap<Class<?>, Class<?>> interfaces = new ConcurrentHashMap<>();

	/** 
	 * @deprecated CDI eyes only
	 */
	protected LinkToHandler() {
		this(null, null, null);
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

	private Lock lock = new ReentrantLock();

	@Override
	public Object get(Object key) {
		logger.debug("getting key {}", key);
		
		BeanClass beanClass = (BeanClass) key;
		final Class<?> controller = beanClass.getType();
		Class<?> linkToInterface = interfaces.get(controller);
		if (linkToInterface == null) {
			logger.debug("interface not found, creating one {}", controller);

			lock.lock();
			try {
				linkToInterface = interfaces.get(controller);
				if (linkToInterface == null) {
					String path = context.getContextPath().replace('/', '$');
					String interfaceName = controller.getName() + "$linkTo" + path;
					linkToInterface = createLinkToInterface(controller, interfaceName);
					interfaces.put(controller, linkToInterface);

					logger.debug("created interface {} to {}", interfaceName, controller);
				}
			} finally {
				lock.unlock();
			}
		}

		return proxifier.proxify(linkToInterface, new MethodInvocation<Object>() {
			@Override
			public Object intercept(Object proxy, Method method, Object[] args, SuperMethod superMethod) {
				String methodName = StringUtils.decapitalize(method.getName().replaceFirst("^get", ""));
				List<Object> params = args.length == 0 ? Collections.emptyList() : Arrays.asList(args);
				return new Linker(controller, methodName, params).getLink();
			}
		});
	}

	private Class<?> createLinkToInterface(final Class<?> controller, String interfaceName) {
		try {
			return Class.forName(interfaceName);
		} catch (ClassNotFoundException e1) {
			// ok, continue
		}

		final Set<CtMethod> used = new HashSet<>();
		ClassPool pool = ClassPool.getDefault();
		CtClass inter = pool.makeInterface(interfaceName);

		try {
			CtClass returnType = pool.get(String.class.getName());
			CtClass objectType = pool.get(Object.class.getName());

			for (Method m : getMethods(controller)) {
				String name = m.getName();

				CtClass[] params = createParameters(objectType, m.getParameterTypes().length);
				CtClass[] empty = new CtClass[0];

				for (int length = params.length; length >= 0; length--) {
					CtMethod method = abstractMethod(returnType, m.getName(), Arrays.copyOf(params, length), empty, inter);
					if (used.add(method)) {
						inter.addMethod(method);
						logger.debug("added method {} to interface {}", method.getName(), controller);
					}
				}

				CtMethod getter = abstractMethod(returnType, String.format("get%s", capitalize(name)), empty, empty, inter);
				if (used.add(getter)) {
					inter.addMethod(getter);
					logger.debug("added getter {} to interface {}", getter.getName(), controller);
				}
			}
			return inter.toClass();
		} catch (CannotCompileException | NotFoundException e) {
			throw new ProxyCreationException(e);
		}
	}

	private CtClass[] createParameters(CtClass objectType, int num) {
		CtClass[] params = new CtClass[num];
		fill(params, objectType);
		
		return params;
	}

	private List<Method> getMethods(Class<?> controller) {
		List<Method> methods = new ArrayList<>();
		
		for (Method method : new Mirror().on(controller).reflectAll().methods()) {
			if (!method.getDeclaringClass().equals(Object.class)) {
				methods.add(method);
			}
		}
		
		sort(methods, new SortByArgumentsLengthDesc());
		return methods;
	}

	private final class SortByArgumentsLengthDesc implements Comparator<Method> {
		@Override
		public int compare(Method o1, Method o2) {
			return Integer.compare(o2.getParameterTypes().length, o1.getParameterTypes().length);
		}
	}

	class Linker {

		private final List<Object> args;
		private final String methodName;
		private final Class<?> controller;

		public Linker(Class<?> controller, String methodName, List<Object> args) {
			this.controller = controller;
			this.methodName = methodName;
			this.args = args;
		}

		public String getLink() {
			Method method = null;

			if (countMethodsWithSameName() > 1) {
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

		private int countMethodsWithSameName() {
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

	/**
	 * Remove generated classes when application stops, due reload context issues.
	 */
	@PreDestroy
	public void removeGeneratedClasses() {
		ClassPool pool = ClassPool.getDefault();
		for (Class<?> clazz : interfaces.values()) {
			CtClass ctClass = pool.getOrNull(clazz.getName());
			if (ctClass != null) {
				ctClass.detach();
				logger.debug("class {} is detached", clazz.getName());
			}
		}
	}
}
