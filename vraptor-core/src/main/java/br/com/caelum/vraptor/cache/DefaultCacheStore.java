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

import static com.google.common.base.Throwables.propagateIfPossible;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.enterprise.inject.Vetoed;

import com.google.common.base.Supplier;

/**
 * A default implementation for {@link CacheStore}. This class is produced by {@link CacheStoreFactory}.
 * 
 * @author Lucas Cavalcanti
 * @author Alberto Souza
 */
@Vetoed
public class DefaultCacheStore<K,V> implements CacheStore<K,V> {

	private final ConcurrentMap<K,V> cache = new ConcurrentHashMap<>();

	@Override
	public V fetch(K key, Supplier<V> valueProvider) {
		if (!cache.containsKey(key)){
			try {
				V value = valueProvider.get();
				cache.put(key, value);
				return value;
			} catch (Exception e) {
				propagateIfPossible(e);
				throw new CacheException("Error computing the value", e);
			}
		}
		return cache.get(key);
	}

	@Override
	public V write(K key, V value) {
		return cache.put(key, value);
	}

	@Override
	public V fetch(K key) {
		return cache.get(key);
	}
}
