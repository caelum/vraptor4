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

import static com.google.common.collect.Collections2.filter;

import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import br.com.caelum.vraptor.cache.CacheStore;
import br.com.caelum.vraptor.controller.BeanClass;
import br.com.caelum.vraptor.controller.ControllerMethod;
import br.com.caelum.vraptor.controller.HttpMethod;
import br.com.caelum.vraptor.core.Converters;
import br.com.caelum.vraptor.http.EncodingHandler;
import br.com.caelum.vraptor.http.MutableRequest;
import br.com.caelum.vraptor.http.ParameterNameProvider;
import br.com.caelum.vraptor.proxy.Proxifier;

import com.google.common.base.Predicate;

/**
 * The default implementation of controller localization rules. It also uses a
 * Path annotation to discover path->method mappings using the supplied
 * ControllerLookupInterceptor.
 *
 * @author Guilherme Silveira
 */
@ApplicationScoped
public class DefaultRouter implements Router {

	private final Collection<Route> routes = new PriorityRoutesList();
	private final Proxifier proxifier;
	private final TypeFinder finder;
	private final Converters converters;
	private final ParameterNameProvider nameProvider;
	private final Evaluator evaluator;
	private final CacheStore<Invocation,Route> cache;
	private final EncodingHandler encodingHandler;

	/** 
	 * @deprecated CDI eyes only
	 */
	protected DefaultRouter() {
		this(null, null, null, null, null, null, null);
	}

	@Inject
	public DefaultRouter(Proxifier proxifier, TypeFinder finder, Converters converters, ParameterNameProvider nameProvider, 
			Evaluator evaluator, EncodingHandler encodingHandler, CacheStore<Invocation,Route> cache) {
		this.proxifier = proxifier;
		this.finder = finder;
		this.converters = converters;
		this.nameProvider = nameProvider;
		this.evaluator = evaluator;
		this.encodingHandler = encodingHandler;
		this.cache = cache;
	}

	@Override
	public RouteBuilder builderFor(String uri) {
		return new DefaultRouteBuilder(proxifier, finder, converters, nameProvider, evaluator, uri, encodingHandler);
	}

	/**
	 * You can override this method to get notified by all added routes.
	 */
	@Override
	public void add(Route r) {
		cacheRoute(r);
		this.routes.add(r);
	}

	@Override
	public ControllerMethod parse(String uri, HttpMethod method, MutableRequest request)
						throws MethodNotAllowedException{
		Collection<Route> routesMatchingUriAndMethod = routesMatchingUriAndMethod(uri, method);

		Iterator<Route> iterator = routesMatchingUriAndMethod.iterator();

		Route route = iterator.next();
		checkIfThereIsAnotherRoute(uri, method, iterator, route);

		return route.controllerMethod(request, uri);
	}

	private void checkIfThereIsAnotherRoute(String uri, HttpMethod method, Iterator<Route> iterator, Route route) {
		if (iterator.hasNext()) {
			Route otherRoute = iterator.next();
			if (route.getPriority() == otherRoute.getPriority()) {
				throw new IllegalStateException(
						MessageFormat.format("There are two rules that matches the uri ''{0}'' with method {1}: {2} with same priority." +
								" Consider using @Path priority attribute.",
								uri, method, Arrays.asList(route, otherRoute)));
			}
		}
	}


	private Collection<Route> routesMatchingUriAndMethod(String uri, HttpMethod method) {
		Collection<Route> routesMatchingMethod = filter(routesMatchingUri(uri), allow(method));
		if (routesMatchingMethod.isEmpty()) {
			EnumSet<HttpMethod> allowed = allowedMethodsFor(uri);
			throw new MethodNotAllowedException(allowed, method.toString());
		}
		return routesMatchingMethod;
	}

	@Override
	public EnumSet<HttpMethod> allowedMethodsFor(String uri) {
		EnumSet<HttpMethod> allowed = EnumSet.noneOf(HttpMethod.class);
		for (Route route : routesMatchingUri(uri)) {
			allowed.addAll(route.allowedMethods());
		}
		return allowed;
	}

	private Collection<Route> routesMatchingUri(String uri) {
		Collection<Route> routesMatchingURI = filter(routes, canHandle(uri));
		if (routesMatchingURI.isEmpty()) {
			throw new ControllerNotFoundException();
		}
		return routesMatchingURI;
	}

	public void cacheRoute(Route r) {
		ControllerMethod controllerMethod = r.getControllerMethod();
		BeanClass controller = controllerMethod.getController();
		Invocation invocation = new Invocation(controller.getType(), controllerMethod.getMethod());
		cache.write(invocation, r);
	}

	@Override
	public <T> String urlFor(Class<T> type, Method method, Object... params) {
		Class<?> rawtype = type;
		if (proxifier.isProxyType(type)) {
			rawtype = type.getSuperclass();
		}
		Invocation invocation = new Invocation(rawtype, method);
		Route route = cache.fetch(invocation);
		if (route == null) {
			throw new RouteNotFoundException("The selected route is invalid for redirection: " + type.getName() + "."
					+ method.getName());
		}
		return route.urlFor(type, method, params);
	}

	@Override
	public List<Route> allRoutes() {
		return Collections.unmodifiableList(new ArrayList<>(routes));
	}

	private Predicate<Route> canHandle(final String uri) {
		return new Predicate<Route>() {
			@Override
			public boolean apply(Route route) {
				return route.canHandle(uri);
			}
		};
	}

	private Predicate<Route> allow(final HttpMethod method) {
		return new Predicate<Route>() {
			@Override
			public boolean apply(Route route) {
				return route.allowedMethods().contains(method);
			}
		};
	}
}

