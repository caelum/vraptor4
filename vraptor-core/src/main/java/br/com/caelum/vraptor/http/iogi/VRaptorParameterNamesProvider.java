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

package br.com.caelum.vraptor.http.iogi;

import java.lang.reflect.AccessibleObject;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import br.com.caelum.vraptor.http.Parameter;
import br.com.caelum.vraptor.http.ParameterNameProvider;

/**
 * An adapter for iogi's parameterNamesProvider
 *
 * @author Lucas Cavalcanti
 * @since
 *
 */
@RequestScoped
public class VRaptorParameterNamesProvider implements br.com.caelum.iogi.spi.ParameterNamesProvider {

	private final ParameterNameProvider parameterNameProvider;

	/** 
	 * @deprecated CDI eyes only
	 */
	protected VRaptorParameterNamesProvider() {
		this(null);
	}

	@Inject
	public VRaptorParameterNamesProvider(ParameterNameProvider parameterNameProvider) {
		this.parameterNameProvider = parameterNameProvider;
	}

	@Override
	public List<String> lookupParameterNames(AccessibleObject methodOrConstructor) {
		List<String> names = new ArrayList<>();
		for (Parameter param : parameterNameProvider.parametersFor(methodOrConstructor)) {
			names.add(param.getName());
		}
		return names;
	}
}
