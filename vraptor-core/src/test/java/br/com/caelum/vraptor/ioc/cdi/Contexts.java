package br.com.caelum.vraptor.ioc.cdi;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.jboss.weld.context.AbstractSharedContext;
import org.jboss.weld.context.ApplicationContext;
import org.jboss.weld.context.bound.BoundConversationContext;
import org.jboss.weld.context.bound.BoundRequestContext;
import org.jboss.weld.context.bound.BoundSessionContext;
import org.jboss.weld.context.bound.MutableBoundRequest;

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

	void startApplicationScope() {
		// Welds ApplicationContext is always active
	}

	void stopApplicationScope() {
		if (applicationContext.isActive()) {
			if (applicationContext instanceof AbstractSharedContext) {
				((AbstractSharedContext) applicationContext).getBeanStore().clear();
			}
		}
	}

	void startSessionScope() {
		sessionContext.associate(sessionMap);
		sessionContext.activate();
	}

	void stopSessionScope() {
		if (sessionContext.isActive()) {
			sessionContext.invalidate();
			sessionContext.deactivate();
			sessionContext.dissociate(sessionMap);
			sessionMap = new HashMap<>();
		}
	}

	void startConversationScope(String cid) {
		conversationContext.associate(new MutableBoundRequest(requestMap,sessionMap));
		conversationContext.activate(cid);
	}

	void stopConversationScope() {
		if (conversationContext.isActive()) {
			conversationContext.invalidate();
			conversationContext.deactivate();
			conversationContext.dissociate(new MutableBoundRequest(requestMap,sessionMap));
		}
	}

	void startRequestScope() {
		requestContext.associate(requestMap);
		requestContext.activate();
	}

	void stopRequestScope() {
		if (requestContext.isActive()) {
			requestContext.invalidate();
			requestContext.deactivate();
			requestContext.dissociate(requestMap);
			requestMap = new HashMap<>();
		}
	}
}
