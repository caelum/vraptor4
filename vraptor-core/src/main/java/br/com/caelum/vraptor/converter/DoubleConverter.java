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

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import br.com.caelum.vraptor.Convert;

/**
 * Localized version of VRaptor's Double converter. If the input value if empty or a null string, null value is 
 * returned. If the input string is not a number a {@link ConversionException} will be throw.
 *
 * @author Otávio Scherer Garcia
 * @since 3.1.2
 */
@Convert(Double.class)
@RequestScoped
public class DoubleConverter implements Converter<Double> {

	private final Locale locale;

	/** 
	 * @deprecated CDI eyes only
	 */
	protected DoubleConverter() {
		this(null);
	}

	@Inject
	public DoubleConverter(Locale locale) {
		this.locale = locale;
	}

	@Override
	public Double convert(String value, Class<? extends Double> type) {
		if (isNullOrEmpty(value)) {
			return null;
		}

		try {
			return getNumberFormat().parse(value).doubleValue();
		} catch (ParseException e) {
			throw new ConversionException(new ConversionMessage("is_not_a_valid_number", value));
		}
	}

	protected NumberFormat getNumberFormat() {
		return DecimalFormat.getInstance(locale);
	}
}
