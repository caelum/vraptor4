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

import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import br.com.caelum.vraptor.Convert;
import br.com.caelum.vraptor.converter.ConversionException;
import br.com.caelum.vraptor.converter.ConversionMessage;
import br.com.caelum.vraptor.converter.Converter;

/**
 * VRaptor converter for {@link LocalDateTime}. {@link LocalDateTime} is part of Joda Time library.
 *
 * @author Lucas Cavalcanti
 */
@Convert(LocalDateTime.class)
public class LocalDateTimeConverter implements Converter<LocalDateTime> {

	private final Locale locale;

	/** 
	 * @deprecated CDI eyes only
	 */
	protected LocalDateTimeConverter() {
		this(null);
	}

	@Inject
	public LocalDateTimeConverter(Locale locale) {
		this.locale = locale;
	}

	@Override
	public LocalDateTime convert(String value, Class<? extends LocalDateTime> type) {
		if (isNullOrEmpty(value)) {
			return null;
		}
		
		try {
			return getFormatter().parseLocalDateTime(value);
		} catch (UnsupportedOperationException | IllegalArgumentException  e) {
			throw new ConversionException(new ConversionMessage("is_not_a_valid_datetime", value));
		}
	}

	protected DateTimeFormatter getFormatter() {
		return DateTimeFormat.shortDateTime().withLocale(locale); 
	}
}