package br.com.caelum.vraptor.interceptor.multipart;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.fileupload.FileItemHeaders;
import org.apache.commons.fileupload.FileItemStream;

public class MockFileItem implements FileItemStream {

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
	public InputStream openStream()
		throws IOException {
		return new ByteArrayInputStream(content);
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
	public String getName() {
		return name;
	}

	@Override
	public boolean isFormField() {
		return formField;
	}

	@Override
	public FileItemHeaders getHeaders() {
		return null;
	}

	@Override
	public void setHeaders(FileItemHeaders arg0) {
	}

}
