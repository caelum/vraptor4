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

package br.com.caelum.vraptor.observer.download;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.slf4j.LoggerFactory.getLogger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;

import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.events.MethodExecuted;

/**
 * Observer that return a File or an InputStream when method return type is a download type.
 *
 * @author filipesabella
 * @author Rodrigo Turini
 */
public class DownloadObserver {

	private static final Logger logger = getLogger(DownloadObserver.class);

	private final HttpServletResponse response;
	private final Result result;

	/**
	 * @deprecated CDI eyes only
	 */
	protected DownloadObserver() {
		this(null, null);
	}

	@Inject
	public DownloadObserver(HttpServletResponse response, Result result) {
		this.response = response;
		this.result = result;
	}

	public void download(@Observes MethodExecuted event)  {

		Object result = event.getMethodInfo().getResult();
		Download download = resolveDownload(result);

		if (download != null) {

			logger.debug("Sending a file to the client");

			if (result == null && this.result.used()) return;
			checkNotNull(result, "You've just returned a Null Download. Consider redirecting to another page/logic");

			try (OutputStream output = response.getOutputStream()) {
				download.write(response);
				output.flush();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

	private Download resolveDownload(Object result) {
		if (result instanceof InputStream) {
			return new InputStreamDownload((InputStream) result, null, null);
		}
		if (result instanceof byte[]) {
			return new ByteArrayDownload((byte[]) result, null, null);
		}
		if (result instanceof File) {
			try {
				return new FileDownload((File) result, null, null);
			} catch (FileNotFoundException e) {
				throw new RuntimeException(e);
			}
		}
		if (result instanceof Download)  {
			return (Download) result;
		}
		return null;
	}
}