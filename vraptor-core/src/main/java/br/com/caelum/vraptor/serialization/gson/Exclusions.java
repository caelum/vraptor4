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
package br.com.caelum.vraptor.serialization.gson;

import static br.com.caelum.vraptor.serialization.gson.GsonSerializer.shouldSerializeField;

import java.lang.reflect.Field;
import java.util.Map.Entry;

import javax.enterprise.inject.Vetoed;

import br.com.caelum.vraptor.core.ReflectionProvider;
import br.com.caelum.vraptor.serialization.Serializee;
import br.com.caelum.vraptor.serialization.SkipSerialization;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

/**
 * 
 * @author Renan Reis
 * @author Guilherme Mangabeira
 * @author Renato R. R. de Oliveira
 */
@Vetoed
public class Exclusions implements ExclusionStrategy {

	private final Serializee serializee;
	private final ReflectionProvider reflectionProvider;

	public Exclusions(Serializee serializee, ReflectionProvider reflectionProvider) {
		this.serializee = serializee;
		this.reflectionProvider = reflectionProvider;
	}

	@Override
	public boolean shouldSkipField(FieldAttributes f) {
		SkipSerialization annotation = f.getAnnotation(SkipSerialization.class);
		if (annotation != null)
			return true;
		
		String fieldName = f.getName();
		Class<?> definedIn = f.getDeclaringClass();

		for (Entry<String, Class<?>> include : serializee.getIncludes().entries()) {
			if (isCompatiblePath(include, definedIn, fieldName)) {
				return false;
			}
		}
		for (Entry<String, Class<?>> exclude : serializee.getExcludes().entries()) {
			if (isCompatiblePath(exclude, definedIn, fieldName)) {
				return true;
			}
		}
		
		Field field = reflectionProvider.getField(definedIn, fieldName);
		return !serializee.isRecursive() && !shouldSerializeField(field.getType());
	}

	private boolean isCompatiblePath(Entry<String, Class<?>> path, Class<?> definedIn, String fieldName) {
		return path.getValue().equals(definedIn) && (path.getKey().equals(fieldName) || 
				path.getKey().endsWith("." + fieldName));
	}

	@Override
	public boolean shouldSkipClass(Class<?> clazz) {
		return clazz.isAnnotationPresent(SkipSerialization.class);
	}
}
