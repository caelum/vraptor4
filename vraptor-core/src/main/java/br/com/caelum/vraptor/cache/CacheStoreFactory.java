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
package br.com.caelum.vraptor.cache;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;

/**
 * A factory class that produces cache implementations.
 * 
 * @author Alberto Souza
 * @author Otávio S Garcia
 */
@Dependent
public class CacheStoreFactory {

	@Produces
	@Default
	public <K, V> DefaultCacheStore<K, V> buildDefaultCache() {
		return new DefaultCacheStore<>();
	}

	@Produces
	@LRU
	public <K, V> LRUCacheStore<K, V> buildLRUCache(InjectionPoint ip) {
		int capacity = ip.getAnnotated().getAnnotation(LRU.class).capacity();
		return new LRUCacheStore<>(capacity);
	}
}