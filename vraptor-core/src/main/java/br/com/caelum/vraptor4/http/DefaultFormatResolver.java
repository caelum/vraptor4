/***
 * Copyright (c) 2009 Caelum - www.caelum.com.br/opensource All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package br.com.caelum.vraptor4.http;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import br.com.caelum.vraptor4.view.AcceptHeaderToFormat;

/**
 * Default implementation for FormatResolver.
 * It tries to use _format parameter, than Accept Header and defaults to html.
 *
 * @author Lucas Cavalcanti
 * @author Jose Donizetti
 * @since 3.0.3
 */
@RequestScoped
public class DefaultFormatResolver implements FormatResolver {

	private HttpServletRequest request;
	private AcceptHeaderToFormat acceptHeaderToFormat;

	@Deprecated // CDI eyes only
	public DefaultFormatResolver() {}

	@Inject
	public DefaultFormatResolver(HttpServletRequest request, AcceptHeaderToFormat acceptHeaderToFormat) {
		this.request = request;
		this.acceptHeaderToFormat = acceptHeaderToFormat;
	}

	public String getAcceptFormat() {
		String format = request.getParameter("_format");
		if (format != null) {
			return format;
		}

		format = request.getHeader("Accept");

		return acceptHeaderToFormat.getFormat(format) ;
	}

}
