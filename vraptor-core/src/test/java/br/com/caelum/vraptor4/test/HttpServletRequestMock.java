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
package br.com.caelum.vraptor4.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.AsyncContext;
import javax.servlet.DispatcherType;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

import br.com.caelum.vraptor4.http.MutableRequest;

/**
 * @author Fabio Kung
 */
public class HttpServletRequestMock implements MutableRequest {
	private final Map<String, Object> attributes = new HashMap<>();
	private HttpSession session;
	private HttpServletRequestWrapper wrapper;

	public boolean authenticate(HttpServletResponse response)
			throws IOException, ServletException {
		return wrapper.authenticate(response);
	}

	public boolean equals(Object obj) {
		return wrapper.equals(obj);
	}

	public AsyncContext getAsyncContext() {
		return wrapper.getAsyncContext();
	}

	public Enumeration<String> getAttributeNames() {
		return wrapper.getAttributeNames();
	}

	public String getAuthType() {
		return wrapper.getAuthType();
	}

	public String getCharacterEncoding() {
		return wrapper.getCharacterEncoding();
	}

	public int getContentLength() {
		return wrapper.getContentLength();
	}

	public String getContentType() {
		return wrapper.getContentType();
	}

	public String getContextPath() {
		return wrapper.getContextPath();
	}

	public Cookie[] getCookies() {
		return wrapper.getCookies();
	}

	public long getDateHeader(String name) {
		return wrapper.getDateHeader(name);
	}

	public DispatcherType getDispatcherType() {
		return wrapper.getDispatcherType();
	}

	public String getHeader(String name) {
		return wrapper.getHeader(name);
	}

	public Enumeration<String> getHeaderNames() {
		return wrapper.getHeaderNames();
	}

	public Enumeration<String> getHeaders(String name) {
		return wrapper.getHeaders(name);
	}

	public ServletInputStream getInputStream() throws IOException {
		return wrapper.getInputStream();
	}

	public int getIntHeader(String name) {
		return wrapper.getIntHeader(name);
	}

	public String getLocalAddr() {
		return wrapper.getLocalAddr();
	}

	public String getLocalName() {
		return wrapper.getLocalName();
	}

	public int getLocalPort() {
		return wrapper.getLocalPort();
	}

	public Locale getLocale() {
		return wrapper.getLocale();
	}

	public Enumeration<Locale> getLocales() {
		return wrapper.getLocales();
	}

	public String getMethod() {
		return wrapper.getMethod();
	}

	public String getParameter(String name) {
		return wrapper.getParameter(name);
	}

	public Map<String, String[]> getParameterMap() {
		return wrapper.getParameterMap();
	}

	public Enumeration<String> getParameterNames() {
		return wrapper.getParameterNames();
	}

	public String[] getParameterValues(String name) {
		return wrapper.getParameterValues(name);
	}

	public Part getPart(String name) throws IOException, ServletException {
		return wrapper.getPart(name);
	}

	public Collection<Part> getParts() throws IOException, ServletException {
		return wrapper.getParts();
	}

	public String getPathInfo() {
		return wrapper.getPathInfo();
	}

	public String getPathTranslated() {
		return wrapper.getPathTranslated();
	}

	public String getProtocol() {
		return wrapper.getProtocol();
	}

	public String getQueryString() {
		return wrapper.getQueryString();
	}

	public BufferedReader getReader() throws IOException {
		return wrapper.getReader();
	}

	public String getRealPath(String path) {
		return wrapper.getServletContext().getRealPath(path);
	}

	public String getRemoteAddr() {
		return wrapper.getRemoteAddr();
	}

	public String getRemoteHost() {
		return wrapper.getRemoteHost();
	}

	public int getRemotePort() {
		return wrapper.getRemotePort();
	}

	public String getRemoteUser() {
		return wrapper.getRemoteUser();
	}

	public ServletRequest getRequest() {
		return wrapper.getRequest();
	}

	public RequestDispatcher getRequestDispatcher(String path) {
		return wrapper.getRequestDispatcher(path);
	}

	public String getRequestURI() {
		return wrapper.getRequestURI();
	}

	public StringBuffer getRequestURL() {
		return wrapper.getRequestURL();
	}

	public String getRequestedSessionId() {
		return wrapper.getRequestedSessionId();
	}

	public String getScheme() {
		return wrapper.getScheme();
	}

	public String getServerName() {
		return wrapper.getServerName();
	}

	public int getServerPort() {
		return wrapper.getServerPort();
	}

	public ServletContext getServletContext() {
		return wrapper.getServletContext();
	}

	public String getServletPath() {
		return wrapper.getServletPath();
	}

	public Principal getUserPrincipal() {
		return wrapper.getUserPrincipal();
	}

	public int hashCode() {
		return wrapper.hashCode();
	}

	public boolean isAsyncStarted() {
		return wrapper.isAsyncStarted();
	}

	public boolean isAsyncSupported() {
		return wrapper.isAsyncSupported();
	}

	public boolean isRequestedSessionIdFromCookie() {
		return wrapper.isRequestedSessionIdFromCookie();
	}

	public boolean isRequestedSessionIdFromURL() {
		return wrapper.isRequestedSessionIdFromURL();
	}

	public boolean isRequestedSessionIdFromUrl() {
		return wrapper.isRequestedSessionIdFromUrl();
	}

	public boolean isRequestedSessionIdValid() {
		return wrapper.isRequestedSessionIdValid();
	}

	public boolean isSecure() {
		return wrapper.isSecure();
	}

	public boolean isUserInRole(String role) {
		return wrapper.isUserInRole(role);
	}

	public boolean isWrapperFor(Class wrappedType) {
		return wrapper.isWrapperFor(wrappedType);
	}

	public boolean isWrapperFor(ServletRequest wrapped) {
		return wrapper.isWrapperFor(wrapped);
	}

	public void login(String username, String password) throws ServletException {
		wrapper.login(username, password);
	}

	public void logout() throws ServletException {
		wrapper.logout();
	}

	public void removeAttribute(String name) {
		wrapper.removeAttribute(name);
	}

	public void setCharacterEncoding(String enc)
			throws UnsupportedEncodingException {
		wrapper.setCharacterEncoding(enc);
	}

	public void setRequest(ServletRequest request) {
		wrapper.setRequest(request);
	}

	public AsyncContext startAsync() throws IllegalStateException {
		return wrapper.startAsync();
	}

	public AsyncContext startAsync(ServletRequest servletRequest,
			ServletResponse servletResponse) throws IllegalStateException {
		return wrapper.startAsync(servletRequest, servletResponse);
	}

	public String toString() {
		return wrapper.toString();
	}

	@Inject
	public HttpServletRequestMock(HttpSession session, final MutableRequest mock) {
		this.session = session;
		this.wrapper = new HttpServletRequestWrapper(mock);
	}

	@Deprecated
	public HttpServletRequestMock() {
	}

	public HttpSession getSession() {
		return session;
	}

	public HttpSession getSession(boolean allowCreate) {
		return session;
	}

	public Object getAttribute(String name) {
		return attributes.get(name);
	}

	public void setAttribute(String name, Object o) {
		attributes.put(name, o);
	}

	public void setParameter(String key, String... value) {
	}

	@Override
	public HttpServletRequest getOriginalRequest() {
		return this;
	}
}
