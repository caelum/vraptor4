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
package br.com.caelum.vraptor.ioc.cdi;

import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.weld.contexts.AbstractSharedContext;
import org.jboss.weld.context.ApplicationContext;
import org.jboss.weld.context.bound.BoundConversationContext;
import org.jboss.weld.context.bound.BoundRequestContext;
import org.jboss.weld.context.bound.BoundSessionContext;
import org.jboss.weld.context.bound.MutableBoundRequest;

@ApplicationScoped
public class Contexts {

	@Inject
	private ApplicationContext applicationContext;

	@Inject
	private BoundSessionContext sessionContext;

	@Inject
	private BoundRequestContext requestContext;

	@Inject
	private BoundConversationContext conversationContext;

	private Map<String, Object> sessionMap = new HashMap<>();;
	private Map<String, Object> requestMap = new HashMap<>();;

	public void startApplicationScope() {
		// Welds ApplicationContext is always active
	}

	public void stopApplicationScope() {
		if (applicationContext.isActive()) {
			if (applicationContext instanceof AbstractSharedContext) {
				((AbstractSharedContext) applicationContext).getBeanStore().clear();
			}
		}
	}

	public void startSessionScope() {
		sessionContext.associate(sessionMap);
		sessionContext.activate();
	}

	public void stopSessionScope() {
		if (sessionContext.isActive()) {
			sessionContext.invalidate();
			sessionContext.deactivate();
			sessionContext.dissociate(sessionMap);
			sessionMap = new HashMap<>();
		}
	}

	public void startConversationScope(String cid) {
		conversationContext.associate(new MutableBoundRequest(requestMap,sessionMap));
		conversationContext.activate(cid);
	}

	public void stopConversationScope() {
		if (conversationContext.isActive()) {
			conversationContext.invalidate();
			conversationContext.deactivate();
			conversationContext.dissociate(new MutableBoundRequest(requestMap,sessionMap));
		}
	}

	public void startRequestScope() {
		requestContext.associate(requestMap);
		requestContext.activate();
	}

	public void stopRequestScope() {
		if (requestContext.isActive()) {
			requestContext.invalidate();
			requestContext.deactivate();
			requestContext.dissociate(requestMap);
			requestMap = new HashMap<>();
		}
	}
}
