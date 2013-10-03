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

import javax.enterprise.inject.Vetoed;

/**
 * A LRU cache based on LinkedHashMap.
 *
 * @author Sérgio Lopes
 * @author Paulo Silveira
 */
//Not registering it because it is already produced by LRUCacheFactory.
@Vetoed
public class LRUCache<K, V> extends LinkedHashMap<K, V> implements Cache<K,V> {
	private static final long serialVersionUID = 1L;
	private int capacity;
	
	public LRUCache(int capacity) {
		super(capacity, 0.75f, true);
		this.capacity = capacity;
	}
	
	protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
		return this.size() > capacity;
	}

	@Override
	public V putIfAbsent(K key, V value) {
		if(!this.containsKey(key)){
			return this.put(key, value);
		}
		return value;
	}
	
}
