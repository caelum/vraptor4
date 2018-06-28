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

package br.com.caelum.vraptor.observer;

import static org.slf4j.LoggerFactory.getLogger;

import java.lang.reflect.Type;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.slf4j.Logger;

import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.core.MethodInfo;
import br.com.caelum.vraptor.events.MethodExecuted;
import br.com.caelum.vraptor.interceptor.TypeNameExtractor;

/**
 * Outjects the result of the method invocation to the desired result
 *
 * @author guilherme silveira
 * @author Rodrigo Turini
 */
@ApplicationScoped
public class OutjectResult {

	private static final Logger logger = getLogger(OutjectResult.class);

	private final TypeNameExtractor extractor;

	/**
	 * @deprecated CDI eyes only
	 */
	protected OutjectResult() {
		this(null);
	}

	@Inject
	public OutjectResult(TypeNameExtractor extractor) {
		this.extractor = extractor;
	}

	public void outject(@Observes MethodExecuted event, Result result, MethodInfo methodInfo) {

		Type returnType = event.getMethodReturnType();

		if (!returnType.equals(Void.TYPE)) {
			String name = extractor.nameFor(returnType);
			Object value = methodInfo.getResult();
			logger.debug("outjecting {}={}", name, value);
			result.include(name, value);
		}
	}
}
