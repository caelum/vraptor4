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

package br.com.caelum.vraptor.ioc.cdi;

import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.Enumeration;

import br.com.caelum.vraptor.ioc.GenericContainerTest;
import br.com.caelum.vraptor.ioc.WhatToDo;

public abstract class AbstractProviderRegisteringComponentsTest extends GenericContainerTest {
    protected int counter;

    @Override
	protected abstract <T> T executeInsideRequest(final WhatToDo<T> execution);

    @Override
    protected void configureExpectations() {
    	Enumeration<String> emptyEnumeration = Collections.enumeration(Collections.<String>emptyList());
    	when(context.getInitParameterNames()).thenReturn(emptyEnumeration);
    	when(context.getAttributeNames()).thenReturn(emptyEnumeration);
   }
}