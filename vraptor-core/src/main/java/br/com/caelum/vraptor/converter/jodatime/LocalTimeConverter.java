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

package br.com.caelum.vraptor.converter.jodatime;

import static com.google.common.base.Strings.isNullOrEmpty;

import java.util.Locale;

import javax.inject.Inject;

import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import br.com.caelum.vraptor.Convert;
import br.com.caelum.vraptor.converter.ConversionException;
import br.com.caelum.vraptor.converter.ConversionMessage;
import br.com.caelum.vraptor.converter.Converter;

/**
 * VRaptor converter for {@link LocalTime}. {@link LocalTime} is part of Joda
 * Time library.
 *
 * @author Lucas Cavalcanti
 */
@Convert(LocalTime.class)
public class LocalTimeConverter implements Converter<LocalTime> {

	private final Locale locale;

	/** 
	 * @deprecated CDI eyes only
	 */
	protected LocalTimeConverter() {
		this(null);
	}

	@Inject
	public LocalTimeConverter(Locale locale) {
		this.locale = locale;
	}

	@Override
	public LocalTime convert(String value, Class<? extends LocalTime> type) {
		if (isNullOrEmpty(value)) {
			return null;
		}
		
		try {
			return getFormatter().parseLocalTime(value);
		} catch (UnsupportedOperationException | IllegalArgumentException  e) {
			throw new ConversionException(new ConversionMessage("is_not_a_valid_time", value));
		}
	}
	
	protected DateTimeFormatter getFormatter() {
		return DateTimeFormat.shortTime().withLocale(locale); 
	}
}