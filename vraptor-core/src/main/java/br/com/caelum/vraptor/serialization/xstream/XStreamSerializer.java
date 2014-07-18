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
package br.com.caelum.vraptor.serialization.xstream;

import static br.com.caelum.vraptor.serialization.xstream.VRaptorClassMapper.isPrimitive;
import static java.util.Objects.requireNonNull;

import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.enterprise.inject.Vetoed;

import br.com.caelum.vraptor.serialization.Serializee;
import br.com.caelum.vraptor.serialization.Serializer;
import br.com.caelum.vraptor.serialization.SerializerBuilder;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.xml.PrettyPrintWriter;

/**
 * A SerializerBuilder based on XStream
 * @author Lucas Cavalcanti
 * @since 3.0.2
 */
@Vetoed
public class XStreamSerializer implements SerializerBuilder {

	private final XStream xstream;
	private final HierarchicalStreamWriter writer;
	private final Serializee serializee;

	public XStreamSerializer(XStream xstream, HierarchicalStreamWriter writer) {
		this.xstream = xstream;
		this.writer = writer;
		this.serializee = ((VRaptorXStream) xstream).getVRaptorMapper().getSerializee();
	}

	public XStreamSerializer(XStream xstream, Writer writer) {
		this(xstream, new PrettyPrintWriter(writer));
	}

	@Override
	public Serializer exclude(String... names) {
		serializee.excludeAll(names);
		return this;
	}

	@Override
	public Serializer excludeAll() {
		serializee.excludeAll();
		return this;
	}

	private void preConfigure(Object obj,String alias) {
		requireNonNull(obj, "You can't serialize null objects");

		xstream.processAnnotations(obj.getClass());

		serializee.setRootClass(obj.getClass());
		setRoot(obj);
		setAlias(obj, alias);
	}

	private void setRoot(Object obj) {
		if (Collection.class.isInstance(obj)) {
			this.serializee.setRoot(normalizeList(obj));
		} else {
			this.serializee.setRoot(obj);
		}
	}

	@SuppressWarnings("unchecked")
	private Collection<Object> normalizeList(Object obj) {
		Collection<Object> list;
		if (hasDefaultConverter()) {
			list = new ArrayList<>((Collection<?>)obj);
		} else {
			list = (Collection<Object>) obj;
		}
		serializee.setElementTypes(findElementTypes(list));
		return list;
	}

	private boolean hasDefaultConverter() {
		return xstream.getConverterLookup().lookupConverterForType(serializee.getRootClass())
				.equals(xstream.getConverterLookup().lookupConverterForType(Object.class));
	}

	private void setAlias(Object obj, String alias) {
		if (alias != null) {
			if (Collection.class.isInstance(obj) && (List.class.isInstance(obj) || hasDefaultConverter())) {
				xstream.alias(alias, List.class);
			}
			xstream.alias(alias, obj.getClass());
		}
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
			if (element != null && !isPrimitive(element.getClass())) {
				set.add(element.getClass());
			}
		}
		return set;
	}

	@Override
	public Serializer include(String... fields) {
		serializee.includeAll(fields);
		return this;
	}

	@Override
	public void serialize() {
		xstream.marshal(serializee.getRoot(), writer);
	}

	@Override
	public Serializer recursive() {
		this.serializee.setRecursive(true);
		return this;
	}
}
