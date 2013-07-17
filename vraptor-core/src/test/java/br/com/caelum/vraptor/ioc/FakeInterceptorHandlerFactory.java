package br.com.caelum.vraptor.ioc;

import br.com.caelum.vraptor.core.InterceptorHandler;
import br.com.caelum.vraptor.core.InterceptorHandlerFactory;

@ApplicationScoped
public class FakeInterceptorHandlerFactory implements InterceptorHandlerFactory{

	public InterceptorHandler handlerFor(Class<?> type) {
		return null;
	}

}
