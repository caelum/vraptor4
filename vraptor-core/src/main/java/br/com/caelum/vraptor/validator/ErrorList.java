package br.com.caelum.vraptor.validator;

import static com.google.common.collect.Multimaps.index;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.google.common.base.Function;
import com.google.common.collect.ForwardingList;

/**
 * Class that represents an error list.
 * 
 * @author Otávio Scherer Garcia
 */
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
	 * field.
	 */
	public Map<String, Collection<Message>> getGrouped() {
		if (grouped == null) {
			grouped = index(delegate, byCategory).asMap();
		}
		return grouped;
	}

	@Override
	protected List<Message> delegate() {
		return delegate;
	}
}
