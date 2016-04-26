/***
 * Copyright (c) 2009 Caelum - www.caelum.com.br/opensource All rights reserved.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package br.com.caelum.vraptor.serialization;

import br.com.caelum.vraptor.View;
import br.com.caelum.vraptor.ioc.Container;
import br.com.caelum.vraptor.validator.I18nMessage;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import java.util.ResourceBundle;

/**
 * Basic i18n messsage json serialization alias.
 *
 * @author Leonardo Wolter
 * @since 3.5
 */
@RequestScoped
public class I18nMessageSerialization implements View {

	private final Container container;
	private final ResourceBundle bundle;
	private I18nMessage i18nMessage;

	/**
	 * @deprecated CDI eyes only
	 */
	protected I18nMessageSerialization() {
		this(null, null);
	}

	@Inject
	public I18nMessageSerialization(Container container, ResourceBundle bundle) {
		this.container = container;
		this.bundle = bundle;
	}

	public I18nMessageSerialization from(String category, String key, Object... params) {
		i18nMessage = new I18nMessage(category, key, params);
		i18nMessage.setBundle(bundle);
		return this;
	}

	public void as(Class<? extends Serialization> method) {
		Serialization serialization = container.instanceFor(method);
		serialization.from(i18nMessage, "message").serialize();
	}
}
