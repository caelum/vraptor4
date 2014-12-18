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

import static br.com.caelum.vraptor.proxy.CDIProxies.unproxifyIfPossible;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Throwables.propagateIfPossible;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.controller.ControllerMethod;
import br.com.caelum.vraptor.controller.DefaultControllerMethod;
import br.com.caelum.vraptor.controller.HttpMethod;
import br.com.caelum.vraptor.core.MethodInfo;
import br.com.caelum.vraptor.http.MutableRequest;
import br.com.caelum.vraptor.http.route.Router;
import br.com.caelum.vraptor.interceptor.TypeNameExtractor;
import br.com.caelum.vraptor.ioc.Container;
import br.com.caelum.vraptor.proxy.MethodInvocation;
import br.com.caelum.vraptor.proxy.Proxifier;
import br.com.caelum.vraptor.proxy.ProxyInvocationException;
import br.com.caelum.vraptor.proxy.SuperMethod;

/**
 * The default implementation of LogicResult.<br>
 * Uses cglib to provide proxies for client side redirect (url creation).
 *
 * @author Guilherme Silveira
 */
@RequestScoped
public class DefaultLogicResult implements LogicResult {

	private static final Logger logger = LoggerFactory.getLogger(DefaultLogicResult.class);

	private final Proxifier proxifier;
	private final Router router;
	private final MutableRequest request;
	private final HttpServletResponse response;
	private final Container container;
	private final PathResolver resolver;
	private final TypeNameExtractor extractor;
	private final FlashScope flash;
	private final MethodInfo methodInfo;

	/** 
	 * @deprecated CDI eyes only
	 */
	protected DefaultLogicResult() {
		this(null, null, null, null, null, null, null, null, null);
	}

	@Inject
	public DefaultLogicResult(Proxifier proxifier, Router router, MutableRequest request, HttpServletResponse response,
			Container container, PathResolver resolver, TypeNameExtractor extractor, FlashScope flash, MethodInfo methodInfo) {
		this.proxifier = proxifier;
		this.response = unproxifyIfPossible(response);
		this.request = unproxifyIfPossible(request);
		this.router = router;
		this.container = container;
		this.resolver = resolver;
		this.extractor = extractor;
		this.flash = flash;
		this.methodInfo = methodInfo;
	}

	/**
	 * This implementation don't actually use request dispatcher for the
	 * forwarding. It runs forwarding logic, and renders its <b>default</b>
	 * view.
	 */
	@Override
	public <T> T forwardTo(final Class<T> type) {
		return proxifier.proxify(type, new MethodInvocation<T>() {

			@Override
			public Object intercept(T proxy, Method method, Object[] args, SuperMethod superMethod) {
				try {
					logger.debug("Executing {}", method);
					ControllerMethod old = methodInfo.getControllerMethod();
					methodInfo.setControllerMethod(DefaultControllerMethod.instanceFor(type, method));
					Object methodResult = method.invoke(container.instanceFor(type), args);
					methodInfo.setControllerMethod(old);

					Type returnType = method.getGenericReturnType();
					if (!(returnType == void.class)) {
						request.setAttribute(extractor.nameFor(returnType), methodResult);
					}
					
					if (response.isCommitted()) {
						logger.debug("Response already commited, not forwarding.");
						return null;
					}
					String path = resolver.pathFor(DefaultControllerMethod.instanceFor(type, method));
					logger.debug("Forwarding to {}", path);
					request.getRequestDispatcher(path).forward(request, response);
					return null;
				} catch (InvocationTargetException e) {
					propagateIfPossible(e.getCause());
					throw new ProxyInvocationException(e);
				} catch (Exception e) {
					throw new ProxyInvocationException(e);
				}
			}
		});
	}

	@Override
	public <T> T redirectTo(final Class<T> type) {
		logger.debug("redirecting to class {}", type.getSimpleName());

		return proxifier.proxify(type, new MethodInvocation<T>() {
			@Override
			public Object intercept(T proxy, Method method, Object[] args, SuperMethod superMethod) {
				checkArgument(acceptsHttpGet(method), "Your logic method must accept HTTP GET method if you want to redirect to it");
				
				try {
					String url = router.urlFor(type, method, args);
					String path = request.getContextPath() + url;
					includeParametersInFlash(type, method, args);

					logger.debug("redirecting to {}", path);
					response.sendRedirect(path);
					return null;
				} catch (IOException e) {
					throw new ProxyInvocationException(e);
				}
			}

		});
	}

	protected <T> void includeParametersInFlash(final Class<T> type, Method method, Object[] args) {
		if (args != null && args.length != 0) {
			flash.includeParameters(DefaultControllerMethod.instanceFor(type, method), args);
		}
	}

	protected boolean acceptsHttpGet(Method method) {
		if (method.isAnnotationPresent(Get.class)) {
			return true;
		}

		for (HttpMethod httpMethod : HttpMethod.values()) {
			if (method.isAnnotationPresent(httpMethod.getAnnotation())) {
				return false;
			}
		}

		return true;
	}

}
