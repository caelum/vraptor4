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
package br.com.caelum.vraptor.util.collections;

import static com.google.common.base.Predicates.instanceOf;
import static com.google.common.collect.Iterables.any;
import static java.util.Arrays.asList;

import java.lang.annotation.Annotation;

import br.com.caelum.vraptor.controller.ControllerMethod;
import br.com.caelum.vraptor.interceptor.Interceptor;

import com.google.common.base.Predicate;

public class Filters {

	public static Predicate<Interceptor> accepts(final ControllerMethod method) {
		return new Predicate<Interceptor>() {
			@Override
			public boolean apply(Interceptor interceptor) {
				return interceptor.accepts(method);
			}
		};
	}

	public static Predicate<Annotation[]> hasAnnotation(final Class<?> annotation) {
		return new Predicate<Annotation[]>() {
			@Override
			public boolean apply(Annotation[] param) {
				return any(asList(param), instanceOf(annotation));
			}
		};
	}
}
