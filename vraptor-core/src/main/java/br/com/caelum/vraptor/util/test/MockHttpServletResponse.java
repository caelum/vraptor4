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
public class MockHttpServletResponse implements HttpServletResponse {
	
	private PrintWriter writer;
	private String contentType;
	private ByteArrayOutputStream content =  new ByteArrayOutputStream();
	private int status;
	
	public PrintWriter getWriter() {
		if (this.writer == null) {
			this.writer = new PrintWriter(content);
		}
		return writer;
	}
	
	public String getContentAsString() {
		return this.content.toString();
	}

	public String getContentType() {
		return this.contentType;
	}
	
	public void setContentType(String type) {
		this.contentType = type;
	}

	public String getCharacterEncoding() {
		return null;
	}

	public ServletOutputStream getOutputStream() throws IOException {
		return null;
	}

	public void setCharacterEncoding(String charset) {
	}

	public void setContentLength(int len) {
	}

	public void setBufferSize(int size) {
	}
	
	public int getBufferSize() {
		return 0;
	}

	public void flushBuffer() throws IOException {
	}

	public void resetBuffer() {
	}

	public boolean isCommitted() {
		return false;
	}

	public void reset() {
	}

	public void setLocale(Locale loc) {
	}

	public Locale getLocale() {
		return null;
	}

	public void addCookie(Cookie cookie) {
		
	}

	public boolean containsHeader(String name) {
		return false;
	}

	public String encodeURL(String url) {
		return null;
	}
	
	public String encodeRedirectURL(String url) {
		return null;
	}

	public String encodeUrl(String url) {
		return null;
	}

	public String encodeRedirectUrl(String url) {
		return null;
	}

	public void sendError(int sc, String msg) throws IOException {
		
	}

	public void sendError(int sc) throws IOException {
		
	}

	public void sendRedirect(String location) throws IOException {
		
	}

	public void setDateHeader(String name, long date) {
		
	}

	public void addDateHeader(String name, long date) {
		
	}
	
	public void setHeader(String name, String value) {
		
	}
	
	public void addHeader(String name, String value) {
		
	}
	
	public void setIntHeader(String name, int value) {
		
	}
	
	public void addIntHeader(String name, int value) {
		
	}
	
	public void setStatus(int sc) {
		this.status = sc;
	}
	
	public void setStatus(int sc, String sm) {
		
	}
	
	public int getStatus() {
		return status;
	}
	
	public String getHeader(String name) {
		return null;
	}
	
	public Collection<String> getHeaders(String name) {
		return null;
	}
	
	public Collection<String> getHeaderNames() {
		return null;
	}
}