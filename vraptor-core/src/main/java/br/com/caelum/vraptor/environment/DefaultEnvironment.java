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
package br.com.caelum.vraptor.environment;

import static br.com.caelum.vraptor.environment.EnvironmentType.DEVELOPMENT;
import static br.com.caelum.vraptor.environment.EnvironmentType.PRODUCTION;
import static br.com.caelum.vraptor.environment.EnvironmentType.TEST;
import static com.google.common.base.Objects.firstNonNull;
import static org.slf4j.LoggerFactory.getLogger;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.NoSuchElementException;
import java.util.Properties;

import javax.enterprise.inject.Vetoed;

import org.slf4j.Logger;

/**
 * A default {@link Environment} based on {@link EnvironmentType}.
 * 
 * @author Alexandre Atoji
 * @author Andrew Kurauchi
 * @author Guilherme Silveira
 * @author Rodrigo Turini
 */
@Vetoed
public class DefaultEnvironment implements Environment {

	private static final Logger LOG = getLogger(DefaultEnvironment.class);

	private final Properties properties = new Properties();
	private final EnvironmentType environmentType;

	/**
	 * @deprecated CDI eyes only
	 */
	public DefaultEnvironment() throws IOException {
		this(null);
	}

	public DefaultEnvironment(EnvironmentType environmentType) throws IOException {
		this.environmentType = firstNonNull(environmentType, EnvironmentType.DEVELOPMENT);
		loadAndPut("environment");
		loadAndPut(getName());
	}

	private void loadAndPut(String environment) throws IOException {
		String name = "/" + environment + ".properties";
		InputStream stream = DefaultEnvironment.class.getResourceAsStream(name);
		Properties properties = new Properties();

		if (stream != null) {
			properties.load(stream);
			this.properties.putAll(properties);
		} else {
			LOG.warn("Could not find the file '{}.properties' to load.", environment);
		}
	}

	@Override
	public boolean supports(String feature) {
		if (has(feature)) {
			return Boolean.parseBoolean(get(feature).trim());
		}
		return false;
	}

	@Override
	public boolean has(String key) {
		return properties.containsKey(key);
	}

	@Override
	public String get(String key) {
		if (!has(key)) {
			throw new NoSuchElementException("Key " + key + " not found in environment " + getName());
		}
		return properties.getProperty(key);
	}

	@Override
	public String get(String key, String defaultValue) {
		if (has(key)) {
			return get(key);
		}
		return defaultValue;
	}

	@Override
	public void set(String key, String value) {
		properties.setProperty(key, value);
	}

	@Override
	public Iterable<String> getKeys() {
		return properties.stringPropertyNames();
	}

	@Override
	public boolean isProduction() {
		return PRODUCTION.equals(environmentType);
	}

	@Override
	public boolean isDevelopment() {
		return DEVELOPMENT.equals(environmentType);
	}

	@Override
	public boolean isTest() {
		return TEST.equals(environmentType);
	}

	@Override
	public URL getResource(String name) {
		URL resource = DefaultEnvironment.class.getResource("/" + getName() + name);
		if (resource != null) {
			return resource;
		}
		return DefaultEnvironment.class.getResource(name);
	}

	@Override
	public String getName() {
		return environmentType.getName();
	}
}