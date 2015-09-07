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

import java.util.Calendar;

import javax.annotation.Priority;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;
import javax.interceptor.Interceptor;
import javax.xml.bind.DatatypeConverter;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 * Converter for {@link Calendar} using ISO8601. 
 * @author Otávio Scherer Garcia
 * @since 4.0.0
 */
@ApplicationScoped
@Alternative
@Priority(Interceptor.Priority.LIBRARY_BEFORE)
public class CalendarConverter implements Converter {
	
	@Override
	public boolean canConvert(Class type) {
		return Calendar.class.isAssignableFrom(type);
	}
	
	@Override
	public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
		String out = DatatypeConverter.printDateTime((Calendar) source);
		writer.setValue(out);
	}
	
	@Override
	public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
		String value = reader.getValue();
		return DatatypeConverter.parseDateTime(value);
	}
}
