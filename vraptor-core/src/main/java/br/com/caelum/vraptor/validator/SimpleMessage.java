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

package br.com.caelum.vraptor.validator;

import static com.google.common.base.MoreObjects.toStringHelper;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Objects;
import java.util.ResourceBundle;

import javax.enterprise.inject.Vetoed;

/**
 * A simple validation message.
 *
 * @author Guilherme Silveira
 */
@Vetoed
public class SimpleMessage implements Message {

	private static final long serialVersionUID = 1L;

	private final String message, category;
	private final Severity severity;
	private final Object[] messageParameters;

	/** 
	 * Creates an instance for {@link SimpleMessage} using {@link Severity#ERROR} as severity.
	 */
	public SimpleMessage(String category, String message, Object... messageParameters) {
		this(category, message, Severity.ERROR, messageParameters);
	}

	/** 
	 * Creates an instance for {@link SimpleMessage}.
	 */
	public SimpleMessage(String category, String message, Severity severity, Object... messageParameters) {
		this.category = category;
		this.message = message;
		this.messageParameters = messageParameters;
		this.severity = severity;
	}

	@Override
	public String getMessage() {
		if (messageParameters != null && messageParameters.length > 0) {
			return MessageFormat.format(message, messageParameters);
		}
		return message;
	}

	@Override
	public Severity getSeverity() {
		return severity;
	}

	@Override
	public String getCategory() {
		return category;
	}

	@Override
	public void setBundle(ResourceBundle bundle) {}

	@Override
	public String toString() {
		return toStringHelper(this).add("category", category).add("message", message)
				.add("severity",severity).add("parameters", messageParameters).toString();
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(category) ^ Objects.hashCode(message) ^ Objects.hash(messageParameters) 
				^ Objects.hashCode(severity);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SimpleMessage other = (SimpleMessage) obj;
		return Objects.equals(category, other.category) && Objects.equals(message, other.message)
			&& ((messageParameters == null &&  other.messageParameters == null)
				|| Arrays.equals(messageParameters, other.messageParameters))
			&& Objects.equals(severity, other.severity);
	}
}
