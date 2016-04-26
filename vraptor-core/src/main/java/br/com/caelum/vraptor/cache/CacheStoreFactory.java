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

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;

import com.google.common.base.Supplier;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;


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
	public <K, V> CacheStore<K, V> buildDefaultCache() {
		return new DefaultCacheStore<>();
	}

	@Produces
	@LRU
	public <K, V> CacheStore<K, V> buildLRUCache(InjectionPoint ip) {
		int capacity = ip.getAnnotated().getAnnotation(LRU.class).capacity();
		return createCacheWrapper(capacity);
	}

	public <V, K> CacheStore<K, V> createCacheWrapper(int capacity) {
		Cache<K, V> guavaCache = CacheBuilder.newBuilder()
			.maximumSize(capacity)
			.build();
		return new GuavaCacheWrapper<>(guavaCache);
	}
	
	public static class  GuavaCacheWrapper<K,V> implements CacheStore<K, V>{

		private Cache<K, V> guavaCache;

		public GuavaCacheWrapper(Cache<K,V> guavaCache) {
			this.guavaCache = guavaCache;
		}

		@Override
		public V write(K key, V value) {
			guavaCache.put(key, value);
			return value;
		}

		@Override
		public V fetch(K key) {
			return guavaCache.getIfPresent(key);
		}

		@Override
		public V fetch(K key, Supplier<V> valueProvider) {
			final Supplier<V> userSuplier = valueProvider;
			try {
				return guavaCache.get(key, new Callable<V>() {

					@Override
					public V call() throws Exception {
						return userSuplier.get();
					}
				});
			} catch (ExecutionException e) {
				throw new CacheException("Error while trying to fetch key: " + key, e);
			}
		}
	}
}
