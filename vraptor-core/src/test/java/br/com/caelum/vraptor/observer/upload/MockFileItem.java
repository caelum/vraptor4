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
package br.com.caelum.vraptor.observer.upload;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemHeaders;

public class MockFileItem implements FileItem {

	private static final long serialVersionUID = 5566658661323774136L;

	private String fieldName;
	private String contentType;
	private String name;
	private byte[] content;
	private boolean formField;

	public MockFileItem(String fieldName, String content) {
		this.fieldName = fieldName;
		this.content = content.getBytes();
		this.formField = true;
	}

	public MockFileItem(String fieldName, String name, byte[] content) {
		this.fieldName = fieldName;
		this.contentType = "application/octet-stream";
		this.name = name;
		this.content = content;
	}

	public MockFileItem(String fieldName, String contentType, String name, byte[] content) {
		this.fieldName = fieldName;
		this.contentType = contentType;
		this.name = name;
		this.content = content;
	}

	@Override
	public void delete() {

	}

	@Override
	public byte[] get() {
		return content;
	}

	@Override
	public String getContentType() {
		return contentType;
	}

	@Override
	public String getFieldName() {
		return fieldName;
	}

	@Override
	public InputStream getInputStream() throws IOException {
		return new ByteArrayInputStream(content);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public OutputStream getOutputStream() throws IOException {
		return null;
	}

	@Override
	public long getSize() {
		return content == null ? 0 : content.length;
	}

	@Override
	public String getString() {
		return new String(content);
	}

	@Override
	public String getString(String charsetName) throws UnsupportedEncodingException {
		try {
			return new String(content, Charset.forName(charsetName));
		}catch (Exception e) {
			throw new UnsupportedEncodingException();
		}
	}

	@Override
	public boolean isFormField() {
		return formField;
	}

	@Override
	public boolean isInMemory() {
		return false;
	}

	@Override
	public void setFieldName(String arg0) {

	}

	@Override
	public void setFormField(boolean arg0) {

	}

	@Override
	public void write(File arg0)
		throws Exception {

	}

	@Override
	public FileItemHeaders getHeaders() {
		return null;
	}

	@Override
	public void setHeaders(FileItemHeaders arg0) {
	}

}
