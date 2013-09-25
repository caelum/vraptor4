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

package br.com.caelum.vraptor.converter.l10n;

import static com.google.common.base.Strings.isNullOrEmpty;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Locale;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Alternative;
import javax.inject.Inject;

import br.com.caelum.vraptor.Convert;
import br.com.caelum.vraptor.Converter;
import br.com.caelum.vraptor.converter.ConversionException;
import br.com.caelum.vraptor.converter.ConversionMessage;

/**
 * Localized version of VRaptor's Float converter. This component is optional and must be declared in web.xml before
 * using. If the input value if empty or a null string, null values are returned. If the input string is not a number a
 * {@link ConversionException} will be throw.
 *
 * @author Rafael Dipold
 * @author Otávio Scherer Garcia
 * @since 3.4-snapshot
 */
@Convert(float.class)
@RequestScoped
@Alternative
public class LocaleBasedPrimitiveFloatConverter
	implements Converter<Float> {

	private Locale locale;

	@Deprecated // CDI eyes only
	public LocaleBasedPrimitiveFloatConverter() {
	}

	@Inject
	public LocaleBasedPrimitiveFloatConverter(Locale locale) {
		this.locale = locale;
	}

	@Override
	public Float convert(String value, Class<? extends Float> type) {
		if (isNullOrEmpty(value)) {
			return 0f;
		}

		try {
			DecimalFormat fmt = ((DecimalFormat) DecimalFormat.getInstance(locale));

			return fmt.parse(value).floatValue();
		} catch (ParseException e) {
			throw new ConversionException(new ConversionMessage("is_not_a_valid_number", value));
		}
	}
}
