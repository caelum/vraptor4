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

import static java.util.Collections.enumeration;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;

/**
 * @author Fabio Kung
 */
public class HttpSessionMock implements HttpSession,Serializable {
	private ServletContext context;
	private String id;

	private long creationTime;
	private long lastAccessedTime;
	private int maxInactiveInterval;
	private final Map<String, Object> attributes = new HashMap<>();
	private boolean isNew;

	public HttpSessionMock(ServletContext context, String id) {
		this.context = context;
		this.id = id;
	}

	/** 
	 * @deprecated CDI eyes only
	 */
	protected HttpSessionMock() {
	}

	@Override
	public long getCreationTime() {
		return creationTime;
	}

	public void setCreationTime(long creationTime) {
		this.creationTime = creationTime;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public long getLastAccessedTime() {
		return lastAccessedTime;
	}

	public void setLastAccessedTime(long lastAccessedTime) {
		this.lastAccessedTime = lastAccessedTime;
	}

	@Override
	public ServletContext getServletContext() {
		return context;
	}

	@Override
	public void setMaxInactiveInterval(int interval) {
		this.maxInactiveInterval = interval;
	}

	@Override
	public int getMaxInactiveInterval() {
		return maxInactiveInterval;
	}

	@Override
	public HttpSessionContext getSessionContext() {
		return new HttpSessionContext() {
			@Override
			public HttpSession getSession(String s) {
				return HttpSessionMock.this;
			}

			@Override
			public Enumeration<String> getIds() {
				return new Enumeration<String>() {
					private boolean hasNext = true;

					@Override
					public boolean hasMoreElements() {
						return hasNext;
					}

					@Override
					public String nextElement() {
						hasNext = false;
						return getId();
					}
				};
			}
		};
	}

	@Override
	public Object getAttribute(String name) {
		return attributes.get(name);  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public Object getValue(String name) {
		return getAttribute(name);
	}

	@Override
	public Enumeration<String> getAttributeNames() {
		return enumeration(attributes.keySet());
	}

	@Override
	public String[] getValueNames() {
		Set<String> names = attributes.keySet();
		return names.toArray(new String[names.size()]);
	}

	@Override
	public void setAttribute(String name, Object value) {
		attributes.put(name, value);
	}

	@Override
	public void putValue(String name, Object value) {
		setAttribute(name, value);
	}

	@Override
	public void removeAttribute(String name) {
		attributes.remove(name);
	}

	@Override
	public void invalidate() {
		attributes.clear();
	}

	@Override
	public boolean isNew() {
		return isNew;
	}

	public void setNew(boolean isNew) {
		this.isNew = isNew;
	}

	@Override
	public void removeValue(String name) {
		removeAttribute(name);
	}
}
