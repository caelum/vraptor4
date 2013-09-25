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

import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.SingleValueConverter;

/**
 * Component used to scan all XStream converters
 *
 * @author Rafael Viana
 * @since 3.4.0
 */
@RequestScoped
public class XStreamConverters {

	private List<Converter> converters;
	private List<SingleValueConverter> singleValueConverters;

	private static final Logger logger = LoggerFactory.getLogger(XStreamConverters.class);

	//CDI eyes only
	@Deprecated
	public XStreamConverters() {
	}

	@Inject
	public XStreamConverters(List<Converter> converters, List<SingleValueConverter> singleValueConverters)
	{
		this.converters = Objects.firstNonNull(converters, Lists.<Converter>newArrayList());
		this.singleValueConverters = Objects.firstNonNull(singleValueConverters, Lists.<SingleValueConverter>newArrayList());
	}

	/**
	 * Method used to register all the XStream converters scanned to a XStream instance
	 * @param xstream
	 */
	public void registerComponents(XStream xstream)
	{
		for(Converter converter : converters)
		{
			xstream.registerConverter(converter);
			logger.debug("registered Xstream converter for {}", converter.getClass().getName());
		}

		for(SingleValueConverter converter : singleValueConverters)
		{
			xstream.registerConverter(converter);
			logger.debug("registered Xstream converter for {}", converter.getClass().getName());
		}
	}

}