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
package br.com.caelum.vraptor.observer.download;

import java.io.IOException;
import java.io.OutputStream;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

import br.com.caelum.vraptor.View;

/**
 * A view implementation that writes a download into response.
 * @author Rodrigo Turini
 * @author Victor Kendy Harada
 */
@Dependent
public class DownloadView implements View {

	private HttpServletResponse response;

	/**
	 * @deprecated CDI eyes only
	 */
	protected DownloadView() {
	}

	@Inject
	public DownloadView(HttpServletResponse response){
		this.response = response;
	}

	public void of(Download download) throws IOException {
		OutputStream output = response.getOutputStream();
		download.write(response);
		output.flush();
	}
}
