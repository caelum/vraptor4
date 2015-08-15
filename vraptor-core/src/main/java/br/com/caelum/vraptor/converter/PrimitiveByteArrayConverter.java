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

import java.nio.charset.Charset;

import javax.annotation.Priority;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;
import javax.inject.Inject;
import javax.interceptor.Interceptor;

import br.com.caelum.vraptor.Convert;
import br.com.caelum.vraptor.http.EncodingHandler;

/**
 * A Byte array converter. Null or empty values are returned as an empty byte array.
 *
 * @author Otávio Scherer Garcia
 * @since 4.2.0
 */
@Convert(byte[].class)
@ApplicationScoped
@Alternative
@Priority(Interceptor.Priority.LIBRARY_BEFORE)
public class PrimitiveByteArrayConverter implements Converter<byte[]> {

	public static final String INVALID_MESSAGE_KEY = "is_not_a_valid_number";

	private final EncodingHandler encodingHandler;

	public PrimitiveByteArrayConverter() {
		this(null);
	}

	/** 
	 * @deprecated CDI eyes only
	 */
	@Inject
	public PrimitiveByteArrayConverter(EncodingHandler encodingHandler) {
		this.encodingHandler = encodingHandler;
	}

	@Override
	public byte[] convert(String value, Class<? extends byte[]> type) {
		if (isNullOrEmpty(value)) {
			return new byte[0];
		}

		return value.getBytes(getCurrentCharset());
	}

	protected Charset getCurrentCharset() {
		return Charset.forName(encodingHandler.getEncoding());
	}
}