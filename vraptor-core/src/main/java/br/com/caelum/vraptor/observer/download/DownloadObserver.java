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

import static org.slf4j.LoggerFactory.getLogger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;

import org.slf4j.Logger;

import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.events.MethodExecuted;

/**
 * Observer that return a File or an InputStream when method return type is a download type.
 *
 * @author filipesabella
 * @author Rodrigo Turini
 */
@ApplicationScoped
public class DownloadObserver {

	private static final Logger logger = getLogger(DownloadObserver.class);

	public void download(@Observes MethodExecuted event, Result result) throws IOException {
		Object methodResult = event.getMethodInfo().getResult();
		Download download = resolveDownload(methodResult);

		if (download != null && !result.used()) {
			logger.debug("Sending a file to the client");
			result.use(DownloadView.class).of(download);
		}
	}

	public Download resolveDownload(Object result) throws IOException {
		if (result instanceof InputStream) {
			return new InputStreamDownload((InputStream) result, null, null);
		}
		if (result instanceof byte[]) {
			return new ByteArrayDownload((byte[]) result, null, null);
		}
		if (result instanceof File) {
			return new FileDownload((File) result, null, null);
		}
		if (result instanceof Download) {
			return (Download) result;
		}

		return null;
	}
}
