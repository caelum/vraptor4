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
package br.com.caelum.vraptor.validator;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.enterprise.inject.Vetoed;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ForwardingList;

/**
 * Class that represents an error list.
 * 
 * @author Otávio Scherer Garcia
 */
@Vetoed
public class ErrorList extends ForwardingList<Message> {

	private static Function<Message, String> byCategory = new Function<Message, String>() {
		@Override
		public String apply(Message input) {
			return input.getCategory();
		}
	};

	private final List<Message> delegate;
	private Map<String, Collection<Message>> grouped;

	public ErrorList(List<Message> delegate) {
		this.delegate = delegate;
	}

	/**
	 * Return messages grouped by category. This method can useful if you want to get messages for a specific
	 * category.
	 */
	public Map<String, Collection<Message>> getGrouped() {
		if (grouped == null) {
			grouped = FluentIterable.from(delegate).index(byCategory).asMap();
		}
		return grouped;
	}

	/**
	 * Return all messages by category. This method can useful if you want to get messages for a specific
	 * category.
	 */
	public Collection<Message> from(String category) {
		return getGrouped().get(category);
	}

	@Override
	protected List<Message> delegate() {
		return delegate;
	}
}
