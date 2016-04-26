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
import static com.google.common.collect.Sets.newHashSet;

import java.util.Set;

import javax.annotation.Priority;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;
import javax.interceptor.Interceptor;

import br.com.caelum.vraptor.Convert;

/**
 * VRaptor's Boolean converter.
 *
 * Supports boolean-like values. Converts to TRUE the following strings : true,
 * 1, yes, y, on Converts to FALSE the following strings: false, 0, no, n, off
 *
 * @author Guilherme Silveira
 */
@Convert(Boolean.class)
@ApplicationScoped
@Alternative
@Priority(Interceptor.Priority.LIBRARY_BEFORE)
public class BooleanConverter implements Converter<Boolean> {

	private static final String INVALID_MESSAGE_KEY = "is_not_a_valid_boolean";

	private static final Set<String> IS_TRUE  = newHashSet("TRUE", "1", "YES", "Y", "ON");
	private static final Set<String> IS_FALSE = newHashSet("FALSE", "0", "NO", "N", "OFF");

	@Override
	public Boolean convert(String value, Class<? extends Boolean> type) {
		if (isNullOrEmpty(value)) {
			return null;
		}

		value = value.toUpperCase();

		if (matches(IS_TRUE, value)) {
			return true;
		} else if (matches(IS_FALSE, value)) {
			return false;
		}

		throw new ConversionException(new ConversionMessage(INVALID_MESSAGE_KEY, value));
	}

	private static boolean matches(Set<String> words, String value) {
		return words.contains(value);
	}
}
