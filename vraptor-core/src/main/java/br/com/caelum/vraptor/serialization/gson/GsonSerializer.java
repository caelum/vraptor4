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

import static java.util.Collections.singletonMap;
import static java.util.Objects.requireNonNull;

import java.io.Writer;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.enterprise.inject.Vetoed;

import com.google.gson.Gson;

import br.com.caelum.vraptor.core.ReflectionProvider;
import br.com.caelum.vraptor.interceptor.TypeNameExtractor;
import br.com.caelum.vraptor.serialization.Serializer;
import br.com.caelum.vraptor.serialization.SerializerBuilder;

/**
 * A SerializerBuilder based on Gson.
 *
 * @author Renan Reis
 * @author Guilherme Mangabeira
 */
@Vetoed
public class GsonSerializer implements SerializerBuilder {

	private final GsonSerializerBuilder builder;
	private final Writer writer;
	private final TypeNameExtractor extractor;
	private final ReflectionProvider reflectionProvider;

	public GsonSerializer(GsonSerializerBuilder builder, Writer writer, TypeNameExtractor extractor, 
			ReflectionProvider reflectionProvider) {
		this.writer = writer;
		this.extractor = extractor;
		this.builder = builder;
		this.reflectionProvider = reflectionProvider;
	}

	@Override
	public Serializer exclude(String... names) {
		builder.getSerializee().excludeAll(names);
		return this;
	}

	@Override
	public Serializer excludeAll() {
		builder.getSerializee().excludeAll();
		return this;
	}

	private void preConfigure(Object obj, String alias) {
		requireNonNull(obj, "You can't serialize null objects");

		builder.getSerializee().setRootClass(obj.getClass());

		if (alias == null) {
			if (Collection.class.isInstance(obj) && (List.class.isInstance(obj))) {
				alias = "list";
			} else {
				alias = extractor.nameFor(builder.getSerializee().getRootClass());
			}
		}

		builder.setAlias(alias);

		setRoot(obj);
	}

	private void setRoot(Object obj) {
		if (Collection.class.isInstance(obj)) {
			builder.getSerializee().setRoot(normalizeList(obj));
		} else {
			builder.getSerializee().setRoot(obj);
		}
	}

	private Collection<Object> normalizeList(Object obj) {
		Collection<Object> list = (Collection<Object>) obj;
		builder.getSerializee().setElementTypes(findElementTypes(list));

		return list;
	}

	@Override
	public <T> Serializer from(T object, String alias) {
		preConfigure(object, alias);
		return this;
	}

	@Override
	public <T> Serializer from(T object) {
		preConfigure(object, null);
		return this;
	}

	private Set<Class<?>> findElementTypes(Collection<Object> list) {
		Set<Class<?>> set = new HashSet<>();
		for (Object element : list) {
			if (element != null && !shouldSerializeField(element.getClass())) {
				set.add(element.getClass());
			}
		}
		return set;
	}

	@Override
	public Serializer include(String... fields) {
		builder.getSerializee().includeAll(fields);
		return this;
	}

	@Override
	public void serialize() {
		builder.setExclusionStrategies(new Exclusions(builder.getSerializee(), reflectionProvider));
		Gson gson = builder.create();
		
		String alias = builder.getAlias();
		Object root = builder.getSerializee().getRoot();

		if (builder.isWithoutRoot()) {
			gson.toJson(root, writer);
		} else {
			gson.toJson(singletonMap(alias, root), writer);
		}
	}
	
	@Override
	public Serializer recursive() {
		builder.getSerializee().setRecursive(true);
		return this;
	}

	static boolean shouldSerializeField(Class<?> type) {
		return type.isPrimitive() || type.isEnum() || Number.class.isAssignableFrom(type) || type.equals(String.class)
				|| Date.class.isAssignableFrom(type) || Calendar.class.isAssignableFrom(type)
				|| Boolean.class.equals(type) || Character.class.equals(type);
	}
}
