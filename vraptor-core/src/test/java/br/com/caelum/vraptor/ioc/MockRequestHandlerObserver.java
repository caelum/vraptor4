package br.com.caelum.vraptor.ioc;

import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Specializes;
import javax.inject.Inject;

import br.com.caelum.vraptor.controller.ControllerNotFoundHandler;
import br.com.caelum.vraptor.controller.MethodNotAllowedHandler;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.events.ControllerFound;
import br.com.caelum.vraptor.events.RequestSucceded;
import br.com.caelum.vraptor.events.VRaptorRequestStarted;
import br.com.caelum.vraptor.http.UrlToControllerTranslator;
import br.com.caelum.vraptor.observer.RequestHandlerObserver;

@Specializes
class MockRequestHandlerObserver extends RequestHandlerObserver{
	private boolean vraptorStackCalled;

	@SuppressWarnings("deprecation")
	public MockRequestHandlerObserver() {}

	@Inject
	public MockRequestHandlerObserver(UrlToControllerTranslator translator,
			ControllerNotFoundHandler controllerNotFoundHandler, MethodNotAllowedHandler methodNotAllowedHandler,
			Event<ControllerFound> controllerFoundEvent, Event<RequestSucceded> endRequestEvent,
			InterceptorStack interceptorStack) {
		super(translator, controllerNotFoundHandler, methodNotAllowedHandler, controllerFoundEvent, endRequestEvent,
				interceptorStack);
		// TODO Auto-generated constructor stub
	}

	public void handle(@Observes VRaptorRequestStarted event) {
		vraptorStackCalled = true;
	}

	public boolean isVraptorStackCalled() {
		return vraptorStackCalled;
	}
}
