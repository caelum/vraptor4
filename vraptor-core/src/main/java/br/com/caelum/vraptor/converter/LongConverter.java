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

import javax.annotation.Priority;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;
import javax.interceptor.Interceptor;

import br.com.caelum.vraptor.Convert;

/**
 * VRaptor's Long converter.
 *
 * @author Guilherme Silveira
 */
@Convert(Long.class)
@ApplicationScoped
@Alternative
@Priority(Interceptor.Priority.LIBRARY_BEFORE)
public class LongConverter implements Converter<Long> {

	public static final String INVALID_MESSAGE_KEY = "is_not_a_valid_number";

	@Override
	public Long convert(String value, Class<? extends Long> type) {
		if (isNullOrEmpty(value)) {
			return null;
		}

		try {
			return Long.valueOf(value);
		} catch (NumberFormatException e) {
			throw new ConversionException(new ConversionMessage(INVALID_MESSAGE_KEY, value));
		}
	}

}
