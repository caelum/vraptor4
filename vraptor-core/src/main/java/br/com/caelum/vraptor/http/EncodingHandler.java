/***
 * Copyright (c) 2009 Caelum - www.caelum.com.br/opensource
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package br.com.caelum.vraptor.http;

import static java.nio.charset.Charset.defaultCharset;

import java.io.UnsupportedEncodingException;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.TransientReference;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.com.caelum.vraptor.VRaptorException;
import br.com.caelum.vraptor.config.BasicConfiguration;

/**
 * {@link EncodingHandler} that uses Encoding from web.xml.
 *
 * @author Lucas Cavalcanti
 */
@ApplicationScoped
public class EncodingHandler {

	private final String encoding;

	/** @Deprecated CDI eyes only */
	protected EncodingHandler() {
		this(null);
	}

	@Inject
	public EncodingHandler(@TransientReference BasicConfiguration configuration) {
		if (configuration == null || configuration.getEncoding() == null) {
			encoding = defaultCharset().name();
		} else {
			encoding = configuration.getEncoding();
		}
	}

	public void setEncoding(HttpServletRequest request, HttpServletResponse response) {
		try {
			request.setCharacterEncoding(getEncoding());
			response.setCharacterEncoding(getEncoding());
		} catch (UnsupportedEncodingException e) {
			throw new VRaptorException(e);
		}
	}

	public String getEncoding() {
		return encoding;
	}
}