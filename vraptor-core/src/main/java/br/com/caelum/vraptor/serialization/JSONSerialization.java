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
package br.com.caelum.vraptor.serialization;


/**
 * Basic json serialization support using a Json SerializerBuilder.
 *
 * @author Lucas Cavalcanti
 * @version 3.0.2
 */
public interface JSONSerialization extends Serialization {

	/**
	 * Key used to search via environment if JSON will be printed with indentation or not. Indentation via
	 * code have more priority than via properties.
	 */
	String ENVIRONMENT_INDENTED_KEY = "br.com.caelum.vraptor.serialization.json.indented";

	/**
	 * Exclude the root alias from serialization.
	 * @since 3.1.2
	 */
	NoRootSerialization withoutRoot();
	
	JSONSerialization indented();

	JSONSerialization version(double versionNumber);
	
	JSONSerialization serializeNulls();

}
