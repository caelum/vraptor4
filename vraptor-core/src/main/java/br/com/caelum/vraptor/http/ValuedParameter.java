package br.com.caelum.vraptor.http;

import java.util.Objects;

/**
 * Represents a parameter with value.
 * 
 * @author Otávio Scherer Garcia
 * @since 4.0
 */
public class ValuedParameter {

	private final Parameter parameter;
	private Object value;

	public ValuedParameter(Parameter parameter, Object value) {
		this.parameter = parameter;
		this.value = value;
	}

	public Parameter getParameter() {
		return parameter;
	}
	
	/**
	 * An alias to getParameter().getName().
	 * @return
	 */
	public String getName() {
		return parameter.getName();
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ValuedParameter) {
			ValuedParameter other = (ValuedParameter) obj;
			return Objects.equals(parameter.getName(), other.getParameter().getName())
					&& Objects.equals(value, other.getValue());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(parameter.getName(), value);
	}

	@Override
	public String toString() {
		return "Parameter: " + parameter.getName() + "=" + value;
	}
}
