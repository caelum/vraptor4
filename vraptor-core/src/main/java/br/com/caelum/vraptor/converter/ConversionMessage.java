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
package br.com.caelum.vraptor.converter;

import static com.google.common.base.MoreObjects.firstNonNull;
import static com.google.common.base.MoreObjects.toStringHelper;
import static com.google.common.base.Strings.emptyToNull;

import java.util.ResourceBundle;

import javax.enterprise.inject.Vetoed;

import br.com.caelum.vraptor.validator.I18nMessage;
import br.com.caelum.vraptor.validator.Message;
import br.com.caelum.vraptor.validator.Severity;

@Vetoed
public class ConversionMessage implements Message {

	private static final long serialVersionUID = 1L;
	private final Message message;
	private String category;
	private final Severity severity;

	public ConversionMessage(String category, Message message) {
		this.message = message;
		this.category = category;
		this.severity = Severity.ERROR;
	}
	
	public ConversionMessage(String category, Message message, Severity severity) {
		this.message = message;
		this.category = category;
		this.severity = severity;
	}

	public ConversionMessage(String key, Object... parameters) {
		this("",new I18nMessage("", key, parameters));
	}

	public ConversionMessage(String key, Severity severity, Object... parameters) {
		this("",new I18nMessage("", key, parameters),severity);
	}

	
	@Override
	public Severity getSeverity() {
		return severity;
	}

	@Override
	public String getMessage() {
		return message.getMessage();
	}

	public Message withCategory(String category) {
		this.category = category;
		return this;
	}

	@Override
	public String getCategory() {
		return firstNonNull(emptyToNull(message.getCategory()), category);
	}

	@Override
	public void setBundle(ResourceBundle bundle) {
		message.setBundle(bundle);
	}

	@Override
	public String toString() {
		return toStringHelper(this).add("category", getCategory()).add("message", getMessage()).toString();
	}
}
