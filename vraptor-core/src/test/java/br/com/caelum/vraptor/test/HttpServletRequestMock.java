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
package br.com.caelum.vraptor.test;

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
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpUpgradeHandler;
import javax.servlet.http.Part;

import br.com.caelum.vraptor.http.MutableRequest;

/**
 * @author Fabio Kung
 */
public class HttpServletRequestMock implements MutableRequest {
	private final Map<String, Object> attributes = new HashMap<>();
	private HttpSession session;
	private HttpServletRequestWrapper wrapper;

	@Override
	public String getRequestedUri() {
		return null;
	}
	
	@Override
	public boolean authenticate(HttpServletResponse response)
			throws IOException, ServletException {
		return wrapper.authenticate(response);
	}

	@Override
	public boolean equals(Object obj) {
		return wrapper.equals(obj);
	}

	@Override
	public AsyncContext getAsyncContext() {
		return wrapper.getAsyncContext();
	}

	@Override
	public Enumeration<String> getAttributeNames() {
		return wrapper.getAttributeNames();
	}

	@Override
	public String getAuthType() {
		return wrapper.getAuthType();
	}

	@Override
	public String getCharacterEncoding() {
		return wrapper.getCharacterEncoding();
	}

	@Override
	public int getContentLength() {
		return wrapper.getContentLength();
	}

	@Override
	public String getContentType() {
		return wrapper.getContentType();
	}

	@Override
	public String getContextPath() {
		return wrapper.getContextPath();
	}

	@Override
	public Cookie[] getCookies() {
		return wrapper.getCookies();
	}

	@Override
	public long getDateHeader(String name) {
		return wrapper.getDateHeader(name);
	}

	@Override
	public DispatcherType getDispatcherType() {
		return wrapper.getDispatcherType();
	}

	@Override
	public String getHeader(String name) {
		return wrapper.getHeader(name);
	}

	@Override
	public Enumeration<String> getHeaderNames() {
		return wrapper.getHeaderNames();
	}

	@Override
	public Enumeration<String> getHeaders(String name) {
		return wrapper.getHeaders(name);
	}

	@Override
	public ServletInputStream getInputStream() throws IOException {
		return wrapper.getInputStream();
	}

	@Override
	public int getIntHeader(String name) {
		return wrapper.getIntHeader(name);
	}

	@Override
	public String getLocalAddr() {
		return wrapper.getLocalAddr();
	}

	@Override
	public String getLocalName() {
		return wrapper.getLocalName();
	}

	@Override
	public int getLocalPort() {
		return wrapper.getLocalPort();
	}

	@Override
	public Locale getLocale() {
		return wrapper.getLocale();
	}

	@Override
	public Enumeration<Locale> getLocales() {
		return wrapper.getLocales();
	}

	@Override
	public String getMethod() {
		return wrapper.getMethod();
	}

	@Override
	public String getParameter(String name) {
		return wrapper.getParameter(name);
	}

	@Override
	public Map<String, String[]> getParameterMap() {
		return wrapper.getParameterMap();
	}

	@Override
	public Enumeration<String> getParameterNames() {
		return wrapper.getParameterNames();
	}

	@Override
	public String[] getParameterValues(String name) {
		return wrapper.getParameterValues(name);
	}

	@Override
	public Part getPart(String name) throws IOException, ServletException {
		return wrapper.getPart(name);
	}

	@Override
	public Collection<Part> getParts() throws IOException, ServletException {
		return wrapper.getParts();
	}

	@Override
	public String getPathInfo() {
		return wrapper.getPathInfo();
	}

	@Override
	public String getPathTranslated() {
		return wrapper.getPathTranslated();
	}

	@Override
	public String getProtocol() {
		return wrapper.getProtocol();
	}

	@Override
	public String getQueryString() {
		return wrapper.getQueryString();
	}

	@Override
	public BufferedReader getReader() throws IOException {
		return wrapper.getReader();
	}

	@Override
	public String getRealPath(String path) {
		return wrapper.getServletContext().getRealPath(path);
	}

	@Override
	public String getRemoteAddr() {
		return wrapper.getRemoteAddr();
	}

	@Override
	public String getRemoteHost() {
		return wrapper.getRemoteHost();
	}

	@Override
	public int getRemotePort() {
		return wrapper.getRemotePort();
	}

	@Override
	public String getRemoteUser() {
		return wrapper.getRemoteUser();
	}

	public ServletRequest getRequest() {
		return wrapper.getRequest();
	}

	@Override
	public RequestDispatcher getRequestDispatcher(String path) {
		return wrapper.getRequestDispatcher(path);
	}

	@Override
	public String getRequestURI() {
		return wrapper.getRequestURI();
	}

	@Override
	public StringBuffer getRequestURL() {
		return wrapper.getRequestURL();
	}

	@Override
	public String getRequestedSessionId() {
		return wrapper.getRequestedSessionId();
	}

	@Override
	public String getScheme() {
		return wrapper.getScheme();
	}

	@Override
	public String getServerName() {
		return wrapper.getServerName();
	}

	@Override
	public int getServerPort() {
		return wrapper.getServerPort();
	}
	
	@Override
	public String getServletPath() {
		return wrapper.getServletPath();
	}

	@Override
	public Principal getUserPrincipal() {
		return wrapper.getUserPrincipal();
	}

	@Override
	public int hashCode() {
		return wrapper.hashCode();
	}

	@Override
	public boolean isAsyncStarted() {
		return wrapper.isAsyncStarted();
	}

	@Override
	public boolean isAsyncSupported() {
		return wrapper.isAsyncSupported();
	}

	@Override
	public boolean isRequestedSessionIdFromCookie() {
		return wrapper.isRequestedSessionIdFromCookie();
	}

	@Override
	public boolean isRequestedSessionIdFromURL() {
		return wrapper.isRequestedSessionIdFromURL();
	}

	@Override
	public boolean isRequestedSessionIdFromUrl() {
		return wrapper.isRequestedSessionIdFromUrl();
	}

	@Override
	public boolean isRequestedSessionIdValid() {
		return wrapper.isRequestedSessionIdValid();
	}

	@Override
	public boolean isSecure() {
		return wrapper.isSecure();
	}

	@Override
	public boolean isUserInRole(String role) {
		return wrapper.isUserInRole(role);
	}

	public boolean isWrapperFor(Class wrappedType) {
		return wrapper.isWrapperFor(wrappedType);
	}

	public boolean isWrapperFor(ServletRequest wrapped) {
		return wrapper.isWrapperFor(wrapped);
	}

	@Override
	public void login(String username, String password) throws ServletException {
		wrapper.login(username, password);
	}

	@Override
	public void logout() throws ServletException {
		wrapper.logout();
	}

	@Override
	public void removeAttribute(String name) {
		wrapper.removeAttribute(name);
	}

	@Override
	public void setCharacterEncoding(String enc)
			throws UnsupportedEncodingException {
		wrapper.setCharacterEncoding(enc);
	}

	public void setRequest(ServletRequest request) {
		wrapper.setRequest(request);
	}

	@Override
	public AsyncContext startAsync() throws IllegalStateException {
		return wrapper.startAsync();
	}

	@Override
	public AsyncContext startAsync(ServletRequest servletRequest,
			ServletResponse servletResponse) throws IllegalStateException {
		return wrapper.startAsync(servletRequest, servletResponse);
	}

	@Override
	public String toString() {
		return wrapper.toString();
	}

	@Inject
	public HttpServletRequestMock(HttpSession session, final MutableRequest mock) {
		this.session = session;
		this.wrapper = new HttpServletRequestWrapper(mock);
	}

	/** 
	 * @deprecated CDI eyes only
	 */
	protected HttpServletRequestMock() {
	}

	@Override
	public HttpSession getSession() {
		return session;
	}

	@Override
	public HttpSession getSession(boolean allowCreate) {
		return session;
	}

	@Override
	public Object getAttribute(String name) {
		return attributes.get(name);
	}

	@Override
	public void setAttribute(String name, Object o) {
		attributes.put(name, o);
	}

	@Override
	public void setParameter(String key, String... value) {
	}

	@Override
	public ServletContext getServletContext() {
		return session.getServletContext();
	}
	
	@Override
	public String changeSessionId() {
		return wrapper.changeSessionId();
	}
	
	@Override
	public long getContentLengthLong() {
		return wrapper.getContentLengthLong();
	}
	
	@Override
	public <T extends HttpUpgradeHandler> T upgrade(Class<T> handlerClass) throws IOException, ServletException {
		return wrapper.upgrade(handlerClass);
	}
}
