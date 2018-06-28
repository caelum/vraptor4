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
package br.com.caelum.vraptor.util.test;

import java.util.Collections;
import java.util.Enumeration;
import java.util.ResourceBundle;

import javax.enterprise.inject.Vetoed;

/**
 * Mocked resource bundle that only returns the own key. Can be useful if you need to test without load a
 * resource bundle.
 * 
 * @author Otávio Scherer Garcia
 * @since 4.0.0
 */
@Vetoed
public class MockResourceBundle extends ResourceBundle {

	/**
	 * Returns nothing.
	 */
	@Override
	public Enumeration<String> getKeys() {
		return Collections.emptyEnumeration();
	}

	/**
	 * Return the same key as value.
	 */
	@Override
	protected Object handleGetObject(String key) {
		return key;
	}
}
