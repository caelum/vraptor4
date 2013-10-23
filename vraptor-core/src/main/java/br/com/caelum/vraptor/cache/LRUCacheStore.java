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
package br.com.caelum.vraptor.cache;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import javax.enterprise.inject.Vetoed;

import com.google.common.base.Throwables;

/**
 * A LRU cache based on LinkedHashMap.
 *
 * @author Sérgio Lopes
 * @author Paulo Silveira
 */
//Not registering it because it is already produced by LRUCacheFactory.
@Vetoed
public class LRUCacheStore<K, V> extends LinkedHashMap<K, V> implements CacheStore<K,V> {
	private static final long serialVersionUID = 1L;
	private int capacity;

	public LRUCacheStore(int capacity) {
		super(capacity, 0.75f, true);
		this.capacity = capacity;
	}

	@Override
	protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
		return size() > capacity;
	}

	@Override
	public V fetch(K key, Callable<V> valueProvider) {
		if (!this.containsKey(key)){
			try {
				V value = valueProvider.call();
				put(key, value);
				return value;
			} catch (Exception e) {
				Throwables.propagateIfPossible(e);
				throw new CacheException("Error computing the value", e);
			}
		}
		return get(key);
	}

	@Override
	public V write(K key, V value) {
		return put(key, value);
	}

	@Override
	public V fetch(K key) {
		return get(key);
	}
}
