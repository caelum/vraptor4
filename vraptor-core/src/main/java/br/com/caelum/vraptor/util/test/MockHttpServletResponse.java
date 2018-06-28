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

package br.com.caelum.vraptor.util.test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Locale;

import javax.enterprise.inject.Vetoed;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * A simple mock for HttpServletResponse.
 *
 * This mock keeps a contentType and a content  
 * thereby you could be able to retrieve the content as String.
 *
 * @author Vinícius Oliveira
 */
@Vetoed
public class MockHttpServletResponse implements HttpServletResponse {
	
	private PrintWriter writer;
	private String contentType;
	private ByteArrayOutputStream content =  new ByteArrayOutputStream();
	private int status;
	
	@Override
	public PrintWriter getWriter() {
		if (this.writer == null) {
			this.writer = new PrintWriter(content);
		}
		return writer;
	}
	
	public String getContentAsString() {
		writer.flush();
		return this.content.toString();
	}

	@Override
	public String getContentType() {
		return this.contentType;
	}
	
	@Override
	public void setContentType(String type) {
		this.contentType = type;
	}

	@Override
	public String getCharacterEncoding() {
		return null;
	}

	@Override
	public ServletOutputStream getOutputStream() throws IOException {
		return null;
	}

	@Override
	public void setCharacterEncoding(String charset) {
	}

	@Override
	public void setContentLength(int len) {
	}

	@Override
	public void setBufferSize(int size) {
	}
	
	@Override
	public int getBufferSize() {
		return 0;
	}

	@Override
	public void flushBuffer() throws IOException {
	}

	@Override
	public void resetBuffer() {
	}

	@Override
	public boolean isCommitted() {
		return false;
	}

	@Override
	public void reset() {
	}

	@Override
	public void setLocale(Locale loc) {
	}

	@Override
	public Locale getLocale() {
		return null;
	}

	@Override
	public void addCookie(Cookie cookie) {
		
	}

	@Override
	public boolean containsHeader(String name) {
		return false;
	}

	@Override
	public String encodeURL(String url) {
		return null;
	}
	
	@Override
	public String encodeRedirectURL(String url) {
		return null;
	}

	@Override
	public String encodeUrl(String url) {
		return null;
	}

	@Override
	public String encodeRedirectUrl(String url) {
		return null;
	}

	@Override
	public void sendError(int sc, String msg) throws IOException {
		
	}

	@Override
	public void sendError(int sc) throws IOException {
		
	}

	@Override
	public void sendRedirect(String location) throws IOException {
		
	}

	@Override
	public void setDateHeader(String name, long date) {
		
	}

	@Override
	public void addDateHeader(String name, long date) {
		
	}
	
	@Override
	public void setHeader(String name, String value) {
		
	}
	
	@Override
	public void addHeader(String name, String value) {
		
	}
	
	@Override
	public void setIntHeader(String name, int value) {
		
	}
	
	@Override
	public void addIntHeader(String name, int value) {
		
	}
	
	@Override
	public void setStatus(int sc) {
		this.status = sc;
	}
	
	@Override
	public void setStatus(int sc, String sm) {
		
	}
	
	@Override
	public int getStatus() {
		return status;
	}
	
	@Override
	public String getHeader(String name) {
		return null;
	}
	
	@Override
	public Collection<String> getHeaders(String name) {
		return null;
	}
	
	@Override
	public Collection<String> getHeaderNames() {
		return null;
	}
	
	@Override
	public void setContentLengthLong(long len) {
	}
}
