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
package br.com.caelum.vraptor4.util.collections;

import static com.google.common.base.Predicates.instanceOf;
import static com.google.common.collect.Iterables.any;
import static java.util.Arrays.asList;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import br.com.caelum.vraptor4.interceptor.Interceptor;
import br.com.caelum.vraptor4x.controller.ControllerMethod;
import br.com.caelum.vraptor4x.controller.HttpMethod;
import br.com.caelum.vraptor4x.http.route.Route;

import com.google.common.base.Predicate;

public class Filters {

	public static Predicate<Interceptor> accepts(final ControllerMethod method) {
		return new Predicate<Interceptor>() {
			public boolean apply(Interceptor interceptor) {
				return interceptor.accepts(method);
			}
		};
	}

	public static Predicate<Route> canHandle(final Class<?> type, final Method method) {
		return new Predicate<Route>() {
			public boolean apply(Route route) {
				return route.canHandle(type, method);
			}
		};
	}

	public static Predicate<Route> canHandle(final String uri) {
		return new Predicate<Route>() {
			public boolean apply(Route route) {
				return route.canHandle(uri);
			}
		};
	}
	public static Predicate<Route> allow(final HttpMethod method) {
		return new Predicate<Route>() {
			public boolean apply(Route route) {
				return route.allowedMethods().contains(method);
			}
		};
	}
	
    public static Predicate<Annotation[]> hasAnnotation(final Class<?> annotation) {
        return new Predicate<Annotation[]>() {
            public boolean apply(Annotation[] param) {
                return any(asList(param), instanceOf(annotation));
            }
        };
    }
}
