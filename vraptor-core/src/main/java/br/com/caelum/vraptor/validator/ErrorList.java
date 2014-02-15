package br.com.caelum.vraptor.validator;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.google.common.base.Function;
import com.google.common.collect.ForwardingList;
import com.google.common.collect.Multimaps;

/**
 * Class that represents an error list.
 * 
 * @author Otávio Scherer Garcia
 */
public class ErrorList extends ForwardingList<Message> {

	private final class GroupByCategory implements Function<Message, String> {
		@Override
		public String apply(Message input) {
			return input.getCategory();
		}
	}

	private final List<Message> delegate;
	private Map<String, Collection<Message>> byCategory;

	public ErrorList(List<Message> delegate) {
		this.delegate = delegate;
	}

	public Map<String, Collection<Message>> asMap() {
		if (byCategory == null) {
			byCategory = Multimaps.index(delegate, new GroupByCategory()).asMap();
		}
		return byCategory;
	}

	@Override
	protected List<Message> delegate() {
		return delegate;
	}
}
