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
package br.com.caelum.vraptor4.view;

import br.com.caelum.vraptor4.controller.ControllerMethod;

/**
 * Component responsible to allow redirects with the same passed parameters
 *
 * @author Lucas Cavalcanti
 * @since 3.4.0
 *
 */
public interface FlashScope {
	void includeParameters(ControllerMethod method, Object[] args);
	Object[] consumeParameters(ControllerMethod method);
}
