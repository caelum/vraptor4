/***
 * Copyright (c) 2009 Caelum - www.caelum.com.br/opensource
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package br.com.caelum.vraptor4.http;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import br.com.caelum.vraptor4.config.BasicConfiguration;

/**
 * Create an instance for {@link EncodingHandler}. If {@link BasicConfiguration#ENCODING} is defined into web.xml,
 * the {@link WebXmlEncodingHandler} instance is created, otherwise {@link NullEncodingHandler} is created.
 * 
 * @author Lucas Cavalcanti
 */
@ApplicationScoped
public class EncodingHandlerFactory{

	private EncodingHandler handler;

	//CDI eyes only
	@Deprecated
	public EncodingHandlerFactory() {
	}
	
	@Inject
	public EncodingHandlerFactory(BasicConfiguration configuration) {
		String encoding = configuration.getEncoding();
		this.handler = (encoding == null) ? new NullEncodingHandler() : new WebXmlEncodingHandler(encoding);
	}
	
	@Produces @javax.enterprise.context.ApplicationScoped
	public EncodingHandler getInstance() {
		return handler;
	}
}
