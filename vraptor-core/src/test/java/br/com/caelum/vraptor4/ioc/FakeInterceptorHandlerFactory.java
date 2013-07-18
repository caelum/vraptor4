package br.com.caelum.vraptor4.ioc;

import br.com.caelum.vraptor4.core.InterceptorHandler;
import br.com.caelum.vraptor4.core.InterceptorHandlerFactory;
import br.com.caelum.vraptor4.ioc.ApplicationScoped;

@ApplicationScoped
public class FakeInterceptorHandlerFactory implements InterceptorHandlerFactory{

	public InterceptorHandler handlerFor(Class<?> type) {
		return null;
	}

}
