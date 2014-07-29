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
package br.com.caelum.vraptor.view;

import br.com.caelum.vraptor.Consumes;

/**
 * Controller used to test Generic Controllers on LinkToHandler
 * @author Nykolas Lima
 *
 */
public class GenericController<T> {
	@Consumes
	public void method(T entity) {
		System.out.println("Do Something");
	}
	
	@Consumes
	public void anotherMethod(T entity, String param) {
		System.out.println("Do Another Thing");
	}

	public void methodWithoutGenericType(String param) {
		System.out.println("Without generic");
	}
}
