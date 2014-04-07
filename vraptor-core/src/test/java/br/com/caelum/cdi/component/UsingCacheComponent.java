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
package br.com.caelum.cdi.component;

import javax.inject.Inject;

import br.com.caelum.vraptor.cache.CacheStore;
import br.com.caelum.vraptor.cache.LRU;

public class UsingCacheComponent {

	@Inject
	@LRU(capacity=200)
	private CacheStore<String,String> cacheLRU;

	@Inject
	private CacheStore<String,String> cache;

	public String putWithLRU(String key,String value) {
		return cacheLRU.write(key, value);
	}

	public String putWithDefault(String key,String value) {
		return cache.write(key, value);
	}
}
