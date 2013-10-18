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
package br.com.caelum.vraptor.converter;

import static com.google.common.base.Strings.isNullOrEmpty;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Locale;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import br.com.caelum.vraptor.Convert;

/**
 * Locale based date converter.
 *
 * @author Guilherme Silveira
 */
@Convert(Date.class)
@RequestScoped
public class LocaleBasedDateConverter implements Converter<Date> {

	private final Locale locale;

	/** 
	 * @deprecated CDI eyes only
	 */
	protected LocaleBasedDateConverter() {
		this(null);
	}

	@Inject
	public LocaleBasedDateConverter(Locale locale) {
		this.locale = locale;
	}

	@Override
	public Date convert(String value, Class<? extends Date> type) {
		if (isNullOrEmpty(value)) {
			return null;
		}

		try {
			return getDateFormat().parse(value);

		} catch (ParseException pe) {
			throw new ConversionException(new ConversionMessage("is_not_a_valid_date", value));
		}
	}

	protected DateFormat getDateFormat() {
		return DateFormat.getDateInstance(DateFormat.SHORT, locale);
	}
}
