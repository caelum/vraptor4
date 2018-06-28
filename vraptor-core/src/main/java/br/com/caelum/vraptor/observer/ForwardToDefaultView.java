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

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.slf4j.Logger;

import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.events.RequestSucceded;
import br.com.caelum.vraptor.events.MethodExecuted;
import br.com.caelum.vraptor.view.Results;

/**
 * Observes {@link MethodExecuted} event, and forwards to the default view if no
 * view was rendered so far.
 * 
 * @author Guilherme Silveira
 * @author Rodrigo Turini
 * @author Victor Harada
 */
@Dependent
public class ForwardToDefaultView {
	
	private final Result result;

	private static final Logger logger = getLogger(ForwardToDefaultView.class);

	/** 
	 * @deprecated CDI eyes only
	 */
	protected ForwardToDefaultView() {
		this(null);
	}

	@Inject
	public ForwardToDefaultView(Result result) {
		this.result = result;
	}

	public void forward(@Observes RequestSucceded event) {
		if (result.used() || event.getResponse().isCommitted()) {
			logger.debug("Request already dispatched and commited somewhere else, not forwarding.");
			return;
		}
		
		logger.debug("forwarding to the dafault page for this logic");
		result.use(Results.page()).defaultView();
	}
}
