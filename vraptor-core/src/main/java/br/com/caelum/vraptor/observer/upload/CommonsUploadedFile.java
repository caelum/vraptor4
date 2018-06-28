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

import static com.google.common.base.Throwables.propagateIfPossible;
import static java.util.Objects.requireNonNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.CopyOption;
import java.nio.file.Path;

import javax.enterprise.inject.Vetoed;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

/**
 * An implementation for {@link UploadedFile} that delegates all operations to Apache Commons 
 * FileUpload API.
 * 
 * @author Otávio Scherer Garcia
 * @since 4.2
 */
@Vetoed
public class CommonsUploadedFile implements UploadedFile {
	private static final String TARGET_CANNOT_BE_NULL = "Target can't be null";

	private final FileItem delegate;

	public CommonsUploadedFile(FileItem fileItem) {
		this.delegate = fileItem;
	}

	@Override
	public String getContentType() {
		return delegate.getContentType();
	}

	@Override
	public InputStream getFile() throws IOException {
		return delegate.getInputStream();
	}

	@Override
	public String getFileName() {
		return FilenameUtils.getName(delegate.getName());
	}

	@Override
	public long getSize() {
		return delegate.getSize();
	}

	@Override
	public void writeTo(File target) throws IOException {
		requireNonNull(target, TARGET_CANNOT_BE_NULL);

		try {
			delegate.write(target);
		} catch (IOException e) {
			throw e;
		} catch (Exception e) {
			propagateIfPossible(e);
			throw new IOException(e);
		}
	}

	@Override
	public void writeTo(Path target, CopyOption... options) throws IOException {
		requireNonNull(target, TARGET_CANNOT_BE_NULL);
		writeTo(target.toFile());
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
