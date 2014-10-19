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

import static br.com.caelum.vraptor.util.StringUtils.capitalize;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import br.com.caelum.vraptor.core.ReflectionProvider;
import br.com.caelum.vraptor.http.Parameter;
import br.com.caelum.vraptor.http.ParameterNameProvider;
/**
 * Discover parameter types
 * @author Lucas Cavalcanti
 *
 */
@ApplicationScoped
public class DefaultTypeFinder implements TypeFinder {

	private final ParameterNameProvider provider;
	private final ReflectionProvider reflectionProvider;
	
	/** 
	 * @deprecated CDI eyes only
	 */
	protected DefaultTypeFinder() {
		this(null, null);
	}
	
	@Inject
	public DefaultTypeFinder(ParameterNameProvider provider, ReflectionProvider reflectionProvider) {
		this.provider = provider;
		this.reflectionProvider = reflectionProvider;
	}
	
	@Override
	public Map<String, Class<?>> getParameterTypes(Method method, String[] parameterPaths) {
		Map<String,Class<?>> types = new HashMap<>();
		Parameter[] parametersFor = provider.parametersFor(method);
		for (String path : parameterPaths) {
			for (Parameter parameter: parametersFor) {
				if (path.startsWith(parameter.getName() + ".") || path.equals(parameter.getName())) {
					String[] items = path.split("\\.");
					Class<?> type = parameter.getType();
					for (int j = 1; j < items.length; j++) {
						String item = items[j];
						try {
							type = reflectionProvider.getMethod(type, "get" + capitalize(item)).getReturnType();
						} catch (Exception e) {
							throw new IllegalArgumentException("Parameters paths are invalid: " + Arrays.toString(parameterPaths) + " for method " + method, e);
						}
					}
					types.put(path, type);
				}
			}
		}
		return types;
	}
}
