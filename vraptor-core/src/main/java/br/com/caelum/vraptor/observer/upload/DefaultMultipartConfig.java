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

package br.com.caelum.vraptor.observer.upload;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.enterprise.context.ApplicationScoped;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Throwables;

/**
 * Default implementation for {@link MultipartConfig}.
 * 
 * TODO: should expose not a directory, but a way to define memory or file usage (commons upload has already a common
 * interface to it).
 *
 * @author Paulo Silveira
 */
@ApplicationScoped
public class DefaultMultipartConfig implements MultipartConfig {

	private final Logger logger = LoggerFactory.getLogger(DefaultMultipartConfig.class);

	@Override
	public long getSizeLimit() {
		return 2 * 1024 * 1024;
	}

	@Override
	public File getDirectory() {
		Path tmp = getTemporaryDirectory();
		if (tmp == null) {
			tmp = createDirInsideApplication();
		}
		
		return tmp.toFile();
	}

	protected Path getTemporaryDirectory() {
		try {
			Path tmp = Files.createTempFile("vraptor", "upload");
			Path parent = tmp.getParent();
			logger.debug("Using temporary directory as {}", parent);
			
			Files.delete(tmp);
			return parent;
		} catch (IOException e) {
			logger.warn("Unable to find temp directory", e);
			return null;
		}
	}
	
	protected Path createDirInsideApplication() {
		logger.debug("Creating a dir inside the application");
		
		Path path = Paths.get(".tmp-multipart-upload");
		
		try {
			path = Files.createDirectories(path);
			logger.debug("Using temporary directory as {}", path);
		} catch (IOException e) {
			logger.error("Unable to use temp directory inside application", e);
			throw Throwables.propagate(e);
		}
		
		return path;
	}
}
