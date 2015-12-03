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
package br.com.caelum.vraptor.util;

import static java.util.Objects.requireNonNull;

import java.util.Enumeration;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.enterprise.inject.Vetoed;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A Resource bundle that doesn't throw exception when there is no resource of given key.
 * It only returns ??? + key + ??? when the key doesn't exist.
 *
 * @author Lucas Cavalcanti
 * @since 3.0.2
 */
@Vetoed
public class SafeResourceBundle extends ResourceBundle {

	private static final Logger logger = LoggerFactory.getLogger(SafeResourceBundle.class);
	private final ResourceBundle delegate;
	private final boolean isDefault;

	public SafeResourceBundle(ResourceBundle delegate) {
		this(delegate, false);
	}
	public SafeResourceBundle(ResourceBundle delegate, boolean isDefault) {
		requireNonNull(delegate, "Bundle should not be null. Please report it to VRaptor devs.");
		this.delegate = delegate;
		this.isDefault = isDefault;
	}

	public boolean isDefault() {
		return isDefault;
	}

	@Override
	public Enumeration<String> getKeys() {
		return delegate.getKeys();
	}

	@Override
	protected Object handleGetObject(String key) {
		try {
			return delegate.getString(key);
		} catch (MissingResourceException e) {
			logger.warn("Resource missed while calling delegate", e);
			return "???" + key + "???";
		}
	}
}
