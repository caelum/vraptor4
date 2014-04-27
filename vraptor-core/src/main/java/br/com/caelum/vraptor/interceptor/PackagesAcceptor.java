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
package br.com.caelum.vraptor.interceptor;

import static java.util.Arrays.asList;

import java.util.List;

import javax.enterprise.context.Dependent;

import br.com.caelum.vraptor.controller.ControllerInstance;
import br.com.caelum.vraptor.controller.ControllerMethod;

import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;

@Dependent
public class PackagesAcceptor implements AcceptsValidator<AcceptsForPackages> {

	private List<String> allowedPackages;

	@Override
	public boolean validate(ControllerMethod method, ControllerInstance instance) {
		String controllerPackageName = instance.getBeanClass().getPackageName();
		
		return FluentIterable.from(allowedPackages)
			.anyMatch(currentOrSubpackage(controllerPackageName));
	}

	private Predicate<String> currentOrSubpackage(final String controllerPackageName) {
		return new Predicate<String>() {
			@Override
			public boolean apply(String input) {
				return controllerPackageName.startsWith(input);
			}
		};
	}

	@Override
	public void initialize(AcceptsForPackages annotation) {
		this.allowedPackages = asList(annotation.value());
	}
}
