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

import static com.google.common.base.Objects.toStringHelper;

import java.text.MessageFormat;
import java.util.ResourceBundle;

import javax.enterprise.inject.Vetoed;


/**
 * In this Message implementation, the message is i18n'ed while the category is literal.
 *
 * The i18n is lazy.
 *
 * @author Lucas Cavalcanti
 * @since 3.1.3
 */
@Vetoed
public class I18nMessage implements Message {

	private static final long serialVersionUID = 1L;

	private final Object category;
	private final String message;
	private final Object[] parameters;
	private transient ResourceBundle bundle;
	private final Severity severity;

	public I18nMessage(I18nParam category, String message, Object... parameters) {
		this.category = category;
		this.message = message;
		this.parameters = parameters;
		this.severity = Severity.ERROR;
	}

	public I18nMessage(String category, String message, Object... parameters) {
		this(category,message,Severity.ERROR, parameters);
	}
	
	public I18nMessage(String category, String message, Severity severity, Object... parameters) {
		this.category = category;
		this.message = message;
		this.parameters = parameters;
		this.severity = severity;
	}

	@Override
	public void setBundle(ResourceBundle bundle) {
		this.bundle = bundle;
	}

	@Override
	public String getMessage() {
		checkBundle();

		return MessageFormat.format(bundle.getString(message), i18n(parameters));
	}
	
	@Override
	public Severity getSeverity() {
		return severity;
	}

	private void checkBundle() {
		if (bundle == null) {
			throw new IllegalStateException("You must set the bundle before using the I18nMessage");
		}
	}

	private Object[] i18n(Object[] parameters) {
		for (int i = 0; i < parameters.length; i++) {
			if (parameters[i] instanceof I18nParam) {
				parameters[i] = ((I18nParam)parameters[i]).getKey(bundle);
			}
		}
		return parameters;
	}

	@Override
	public String getCategory() {
		if (category instanceof I18nParam) {
			checkBundle();

			return ((I18nParam) category).getKey(bundle);
		}

		return category.toString();
	}

	@Override
	public String toString() {
		return toStringHelper(this).add("category", category).add("message", message).add("severity",severity).add("parameters", parameters).toString();
	}

}
