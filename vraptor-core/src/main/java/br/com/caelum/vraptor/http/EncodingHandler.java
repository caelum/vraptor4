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

import static com.google.common.base.MoreObjects.firstNonNull;
import static java.nio.charset.Charset.defaultCharset;

import java.io.UnsupportedEncodingException;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.com.caelum.vraptor.VRaptorException;

/**
 * {@link EncodingHandler} that uses Encoding from web.xml.
 *
 * @author Lucas Cavalcanti
 */
@ApplicationScoped
public class EncodingHandler {

	/**
	 * context parameter that represents application character encoding
	 */
	public static final String ENCODING_KEY = "br.com.caelum.vraptor.encoding";

	private String encoding;
	private final ServletContext context;
	
	/** 
	 * @deprecated CDI eyes only
	 */
	protected EncodingHandler() {
		this(null);
	}

	@Inject
	public EncodingHandler(ServletContext context) {
		this.context = context;
	}
	
	@PostConstruct
	public void init() {
		encoding = context.getInitParameter(ENCODING_KEY);
		encoding = firstNonNull(encoding, defaultCharset().name());
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
