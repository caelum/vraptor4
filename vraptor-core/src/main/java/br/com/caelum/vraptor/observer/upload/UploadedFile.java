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
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.CopyOption;
import java.nio.file.Path;

/**
 * An interface which represents the information of an uploaded file.
 *
 * @author Guilherme Silveira
 * @author Paulo Silveira
 * @author Lucas Cavalcanti
 * @author Otávio Garcia
 */
public interface UploadedFile {

	/**
	 * Returns the contentType.
	 */
	String getContentType();

	/**
	 * Returns the contents of uploaded file.
	 */
	InputStream getFile() throws IOException;

	/**
	 * Returns the fileName of the uploaded as it was uploaded from the client.
	 */
	String getFileName();
	
	/**
	 * Returns the size of uploaded file.
	 */
	long getSize();

	/**
	 * Write the current uploaded file to disk.
	 * 
	 * @param target The target file.
	 * @throws IOException if an I/O error occurs
	 */
	void writeTo(File target) throws IOException;

	/**
	 * Write the current uploaded file to disk. You can define some options when move/copy the file like REPLACE_EXISTING, but there is no
	 * guarantees that all implementations supports all options.
	 *
	 * @param target The target file.
	 * @param options Options when write the file.
	 * @throws IOException if an I/O error occurs
	 */
	void writeTo(Path target, CopyOption... options) throws IOException;

	/**
	 * Write the current uploaded file to a {@link OutputStream}.
	 *
	 * @param target The target stream.
	 * @throws IOException if an I/O error occurs
	 */
	void writeTo(OutputStream target) throws IOException;
}
