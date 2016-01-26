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
package br.com.caelum.vraptor.http.route;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.enterprise.inject.Vetoed;

/**
 * Order Routes by priority
 * @author Lucas Cavalcanti
 *
 */
@Vetoed
public class PriorityRoutesList implements Collection<Route> {

	private final SortedMap<Integer, Set<Route>> map;

	private List<Route> cache;

	private final Lock cacheLock = new ReentrantLock();

	public PriorityRoutesList() {
		map = new TreeMap<>();
	}

	private List<Route> getFullList() {
		try {
			cacheLock.lock();
			if (cache == null) {
				cache = new LinkedList<>();
				for (Entry<Integer, Set<Route>> entry : map.entrySet()) {
					cache.addAll(entry.getValue());
				}
			}
			return cache;
		} finally {
			cacheLock.unlock();
		}
	}
	private Set<Route> getSetFor(Route e) {
		if (!map.containsKey(e.getPriority())) {
			map.put(e.getPriority(), new LinkedHashSet<Route>());
		}
		return map.get(e.getPriority());
	}

	@Override
	public boolean add(Route e) {
		cache = null;
		return getSetFor(e).add(e);
	}


	@Override
	public boolean addAll(Collection<? extends Route> c) {
		for (Route route : c) {
			add(route);
		}
		return true;
	}

	@Override
	public void clear() {
		cache = null;
		map.clear();
	}

	@Override
	public boolean contains(Object o) {
		return getFullList().contains(o);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return getFullList().containsAll(c);
	}

	@Override
	public boolean isEmpty() {
		return getFullList().isEmpty();
	}

	@Override
	public Iterator<Route> iterator() {
		return getFullList().iterator();
	}

	@Override
	public boolean remove(Object o) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int size() {
		return getFullList().size();
	}

	@Override
	public Object[] toArray() {
		return getFullList().toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return getFullList().toArray(a);
	}

}
