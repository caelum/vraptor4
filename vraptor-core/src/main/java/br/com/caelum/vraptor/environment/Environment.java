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

import java.net.URL;

/**
 * An environment has a set of key/value properties to be used within your application
 * 
 * @author Alexandre Atoji
 * @author Guilherme Silveira
 */
public interface Environment {

	/**
	 * Returns the environment name
	 * 
	 */
	String getName();

	/**
	 * Checks if a key is present
	 * 
	 */
	boolean has(String key);

	/**
	 * Checks if a key is equals to true if it's not present will return false
	 */
	boolean supports(String feature);

	/**
	 * Returns a key
	 */
	String get(String string);

	/**
	 * Returns a key or a default value if the value isn't set
	 */
	String get(String string, String defaultValue);

	/**
	 * Sets a key in memory. This will *not* affect any configuration file.
	 */
	void set(String key, String value);

	/**
	 * @return an {@link Iterable} with all keys
	 */
	Iterable<String> getKeys();

	/**
	 * An alias to {@link Environment#supports(String)} with {@link EnvironmentType#PRODUCTION}.
	 */
	boolean isProduction();

	/**
	 * An alias to {@link Environment#supports(String)} with {@link EnvironmentType#DEVELOPMENT}.
	 */
	boolean isDevelopment();

	/**
	 * An alias to {@link Environment#supports(String)} with {@link EnvironmentType#TEST}.
	 */
	boolean isTest();

	/**
	 * Locates a resource according to your current environment.
	 */
	URL getResource(String name);
}
