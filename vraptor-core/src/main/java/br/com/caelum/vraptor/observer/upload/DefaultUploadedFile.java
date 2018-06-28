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

import static java.nio.file.Files.copy;
import static java.util.Objects.requireNonNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.CopyOption;
import java.nio.file.Path;

import javax.enterprise.inject.Vetoed;

import org.apache.commons.io.IOUtils;

/**
 * Default implementation for {@link UploadedFile}.
 */
@Vetoed
public class DefaultUploadedFile implements UploadedFile {
	private static final String TARGET_CANNOT_BE_NULL = "Target can't be null";

	private final String contentType;
	private final String fileName;
	private final InputStream content;
	private final long size;
	
	public DefaultUploadedFile(InputStream content, String fileName, String contentType, long size) {
		this.content = content;
		this.fileName = fileName;
		this.contentType = contentType;
		this.size = size;
	}

	@Override
	public String getContentType() {
		return this.contentType;
	}

	@Override
	public InputStream getFile() throws IOException {
		return content;
	}

	@Override
	public String getFileName() {
		return this.fileName;
	}

	@Override
	public long getSize() {
		return size;
	}

	@Override
	public void writeTo(File target) throws IOException {
		requireNonNull(target, TARGET_CANNOT_BE_NULL);
		writeTo(target.toPath());
	}

	@Override
	public void writeTo(Path target, CopyOption... options) throws IOException {
		requireNonNull(target, TARGET_CANNOT_BE_NULL);
		try (InputStream in = getFile()) {
			copy(in, target, options);
		}
	}

	@Override
	public void writeTo(OutputStream target) throws IOException {
		requireNonNull(target, TARGET_CANNOT_BE_NULL);
		IOUtils.copy(getFile(), target);
	}

	@Override
	public String toString() {
		return String.format("UploadedFile[name=%s]", getFileName());
	}
}
