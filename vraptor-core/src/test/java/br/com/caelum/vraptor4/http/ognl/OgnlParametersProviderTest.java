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

package br.com.caelum.vraptor4.http.ognl;

import br.com.caelum.vraptor4.http.ParametersProvider;
import br.com.caelum.vraptor4.http.ParametersProviderTest;
import br.com.caelum.vraptor4.proxy.JavassistProxifier;
import br.com.caelum.vraptor4.proxy.Proxifier;
import br.com.caelum.vraptor4.proxy.ReflectionInstanceCreator;

public class OgnlParametersProviderTest extends ParametersProviderTest {

	@Override
	protected ParametersProvider getProvider() {
		EmptyElementsRemoval removal = new EmptyElementsRemoval();
        Proxifier proxifier = new JavassistProxifier(new ReflectionInstanceCreator());
		return new OgnlParametersProvider(nameProvider, request, container, new OgnlFacade(converters, removal, proxifier));
	}

}
