/***
 * Copyright (c) 2009 Caelum - www.caelum.com.br/opensource
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package br.com.caelum.vraptor.observer.upload;

import static com.google.common.base.Strings.nullToEmpty;
import static org.slf4j.LoggerFactory.getLogger;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;

import br.com.caelum.vraptor.events.ControllerFound;

/**
 * This observer will warn a message in console when no Apache Commons FileUpload 
 * was found in classpath and application try to upload any files.
 *
 * @author Otávio Scherer Garcia
 * @author Rodrigo Turini
 * @since 3.1.3
 */
@ApplicationScoped
public class NullMultipartObserver {

	private static final Logger logger = getLogger(NullMultipartObserver.class);

	public void nullUpload(@Observes ControllerFound event, HttpServletRequest request) {
		if (request.getMethod().toUpperCase().equals("POST")
				&& nullToEmpty(request.getContentType()).startsWith("multipart/form-data")) {
			logger.warn("There is no file upload handlers registered. If you are willing to "
					+ "upload a file, please add the commons-fileupload in your classpath");
		}
	}
}
