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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.enterprise.inject.Vetoed;
import javax.servlet.http.HttpServletResponse;

/**
 * Reads bytes from a file into the result.
 *
 * @author filipesabella
 * @author Paulo Silveira
 * 
 * @see InputStreamDownload
 * @see ByteArrayDownload
 */
@Vetoed
public class FileDownload implements Download {
	private final File file;
	private final String contentType;
	private final String fileName;
	private final boolean doDownload;

	public FileDownload(File file, String contentType, String fileName) throws FileNotFoundException {
		this(file, contentType, fileName, false);
	}

	public FileDownload(File file, String contentType) throws FileNotFoundException {
		this(file, contentType, file.getName(), false);
	}

	public FileDownload(File file, String contentType, String fileName, boolean doDownload) throws FileNotFoundException {
		this.file = checkFile(file);
		this.contentType = contentType;
		this.fileName = fileName;
		this.doDownload = doDownload;
	}
	
	@Override
	public void write(HttpServletResponse response) throws IOException {
		try (InputStream stream = new FileInputStream(file)) {
			Download download = new InputStreamDownload(stream, contentType, fileName, doDownload, file.length());
			download.write(response);
		}
	}
	
	private static File checkFile(File file) throws FileNotFoundException {
		if (!file.exists()) {
			throw new FileNotFoundException("File " + file.getName() + " doesn't exists");
		}
		
		return file;
	}
}
