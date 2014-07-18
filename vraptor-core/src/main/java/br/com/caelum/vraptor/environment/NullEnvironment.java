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

import javax.enterprise.inject.Vetoed;

/**
 * Environment implementation that doesn't' anything.
 * @author Otávio Scherer Garcia
 * @since 4.0.1
 */
@Vetoed
public class NullEnvironment implements Environment {

	@Override
	public String getName() {
		return null;
	}

	@Override
	public boolean has(String key) {
		return false;
	}

	@Override
	public boolean supports(String feature) {
		return false;
	}

	@Override
	public String get(String string) {
		return null;
	}

	@Override
	public String get(String string, String defaultValue) {
		return null;
	}

	@Override
	public void set(String key, String value) {
		
	}

	@Override
	public Iterable<String> getKeys() {
		return null;
	}

	@Override
	public boolean isProduction() {
		return false;
	}

	@Override
	public boolean isDevelopment() {
		return false;
	}

	@Override
	public boolean isTest() {
		return false;
	}

	@Override
	public URL getResource(String name) {
		return null;
	}
}
