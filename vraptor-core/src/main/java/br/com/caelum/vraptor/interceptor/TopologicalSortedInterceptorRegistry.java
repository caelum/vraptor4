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
package br.com.caelum.vraptor.interceptor;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;

import br.com.caelum.vraptor.Intercepts;

/**
 * An interceptor registry that sorts interceptors based on their before and after conditions
 *
 * @author Lucas Cavalcanti
 * @author David Paniz
 * @since 3.3.0
 *
 */
@ApplicationScoped
public class TopologicalSortedInterceptorRegistry implements InterceptorRegistry {

	private final Graph<Class<?>> set = new Graph<>();

	@Override
	public List<Class<?>> all() {
		return set.topologicalOrder();
	}

	@Override
	public void register(Class<?>... interceptors) {
		for (Class<?> interceptor : interceptors) {
			Intercepts intercepts = interceptor.getAnnotation(Intercepts.class);
			if (intercepts != null) {
				addEdges(interceptor, intercepts.before(), intercepts.after());
			} 
		}
	}

	private void addEdges(Class<?> interceptor, Class<?>[] before, Class<?>[] after) {
		set.addEdges(interceptor, before);

		for (Class<?> other : after) {
			set.addEdge(other, interceptor);
		}
	}
}
