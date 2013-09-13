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

package br.com.caelum.vraptor4.http;

import java.io.UnsupportedEncodingException;

import javax.enterprise.inject.Vetoed;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.com.caelum.vraptor4.VRaptorException;

/**
 * {@link EncodingHandler} that does nothing.
 * 
 * @author Lucas Cavalcanti
 */
@Vetoed
public class UTF8EncodingHandler implements EncodingHandler {
	
	private final String DEFAULT_ENCODING = "UTF-8";
    
	public void setEncoding(HttpServletRequest request, HttpServletResponse response) {
		try {
			request.setCharacterEncoding(DEFAULT_ENCODING);
			response.setCharacterEncoding(DEFAULT_ENCODING);
		} catch (UnsupportedEncodingException e) {
			throw new VRaptorException(e);
		}
	}

	@Override
	public String getEncoding() {
		return DEFAULT_ENCODING;
	}
}
