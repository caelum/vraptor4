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

package br.com.caelum.vraptor4.http.route;

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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import br.com.caelum.vraptor4.controller.BeanClass;
import br.com.caelum.vraptor4.controller.ControllerMethod;
import br.com.caelum.vraptor4.controller.HttpMethod;
import br.com.caelum.vraptor4.core.Converters;
import br.com.caelum.vraptor4.http.EncodingHandler;
import br.com.caelum.vraptor4.http.MutableRequest;
import br.com.caelum.vraptor4.http.ParameterNameProvider;
import br.com.caelum.vraptor4.proxy.Proxifier;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;

/**
 * The default implementation of controller localization rules. It also uses a
 * Path annotation to discover path->method mappings using the supplied
 * ControllerLookupInterceptor.
 *
 * @author Guilherme Silveira
 */
@ApplicationScoped
public class DefaultRouter implements Router {

	private  Proxifier proxifier;
	private final  Collection<Route> routes = new PriorityRoutesList();
	private  TypeFinder finder;
	private  Converters converters;
	private  ParameterNameProvider nameProvider;
    private  Evaluator evaluator;
    private ConcurrentHashMap<Invocation, Route> cache = new ConcurrentHashMap<Invocation, Route>();
	private EncodingHandler encodingHandler;

    //CDI eyes only
	@Deprecated
	public DefaultRouter() {
	}

    @Inject
    public DefaultRouter(RoutesConfiguration config,
			Proxifier proxifier, TypeFinder finder, Converters converters, 
			ParameterNameProvider nameProvider, Evaluator evaluator, EncodingHandler encodingHandler) {
		this.proxifier = proxifier;
		this.finder = finder;
		this.converters = converters;
		this.nameProvider = nameProvider;
        this.evaluator = evaluator;
		this.encodingHandler = encodingHandler;
		config.config(this);
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
		cache.putIfAbsent(invocation, r);
	}

	@Override
	public <T> String urlFor(Class<T> type, Method method, Object... params) {
		Class<?> rawtype = type;
		if (proxifier.isProxyType(type)) {
			rawtype = type.getSuperclass();
		}
		Invocation invocation = new Invocation(rawtype, method);
		Route route = cache.get(invocation);
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

	private static class Invocation {
		private Class<?> controllerType;
		private Method method;

		public Invocation(Class<?> type, Method method) {
			controllerType = type;
			this.method = method;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime
					* result
					+ ((controllerType == null) ? 0 : controllerType.hashCode());
			result = prime * result
					+ ((method == null) ? 0 : method.getName().hashCode());
			result = prime * result
					+ ((method == null) ? 0 : Arrays.hashCode(method.getParameterTypes()));
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Invocation other = (Invocation) obj;
			if (controllerType == null) {
				if (other.controllerType != null)
					return false;
			} else if (!controllerType.equals(other.controllerType))
				return false;
			if (method == null) {
				if (other.method != null)
					return false;
			} else if (method.getName().equals(other.method.getName())
					&& Arrays.equals(method.getParameterTypes(), other.method.getParameterTypes()))
				return true;
			return false;
		}
		
	}
	
    private Predicate<Route> canHandle(final String uri) {
        return new Predicate<Route>() {
            public boolean apply(Route route) {
                return route.canHandle(uri);
            }
        };
    }
    
    private Predicate<Route> allow(final HttpMethod method) {
        return new Predicate<Route>() {
            public boolean apply(Route route) {
                return route.allowedMethods().contains(method);
            }
        };
    }
}

