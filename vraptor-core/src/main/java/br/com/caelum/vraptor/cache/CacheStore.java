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

import com.google.common.base.Supplier;

/**
 * An API to use as internal VRaptor cache
 * 
 * @author Lucas Cavalcanti
 * @author Alberto Souza
 * @since 4.0.0
 * @param <K> type of the cache key
 * @param <V> type of the cache value
 */
public interface CacheStore<K,V> {

	/**
	 * Writes a value on the cache
	 *
	 * @param key
	 * @param value
	 * @return the previous stored value, null if none.
	 */
	public V write(K key, V value);

	/**
	 * Fetches the value under the key
	 * @param key
	 * @return the stored value for the key, or null.
	 */
	public V fetch(K key);

	/**
	 * Fetches the value under the key. If no value is set, valueProvider is used to
	 * compute a value for the key. Once the value is computed, it is stored on the cache.
	 *
	 * @param key
	 * @param valueProvider
	 * @return the stored or the new value for the provided key.
	 */
	public V fetch(K key, Supplier<V> valueProvider);

}
