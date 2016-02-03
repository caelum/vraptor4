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
package br.com.caelum.vraptor.http;

import java.util.Objects;

import javax.enterprise.inject.Vetoed;

/**
 * Represents a parameter with value.
 * 
 * @author Otávio Scherer Garcia
 * @since 4.0
 */
@Vetoed
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
		if (this.getClass() == obj.getClass()) {
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
