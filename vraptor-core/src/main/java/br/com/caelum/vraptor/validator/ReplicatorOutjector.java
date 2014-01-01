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

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.core.MethodInfo;
import br.com.caelum.vraptor.http.ValuedParameter;

/**
 * Outjector implementation that replicates logic parameters to next logic
 * @author Lucas Cavalcanti
 * @since 3.1.1
 */
@RequestScoped
public class ReplicatorOutjector implements Outjector {

	private final Result result;
	private final MethodInfo methodInfo;

	/** 
	 * @deprecated CDI eyes only
	 */
	protected ReplicatorOutjector() {
		this(null, null);
	}

	@Inject
	public ReplicatorOutjector(Result result, MethodInfo method) {
		this.result = result;
		this.methodInfo = method;
	}

	@Override
	public void outjectRequestMap() {
		for (ValuedParameter vparameter : methodInfo.getValuedParameters()) {
			result.include(vparameter.getName(), vparameter.getValue());
		}
	}
}
