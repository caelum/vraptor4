package br.com.caelum.vraptor.ioc;

import br.com.caelum.vraptor.core.InterceptorHandler;
import br.com.caelum.vraptor.core.InterceptorHandlerFactory;
import br.com.caelum.vraptor.interceptor.Interceptor;

@ApplicationScoped
public class FakeInterceptorHandlerFactory implements InterceptorHandlerFactory{

	public InterceptorHandler handlerFor(Class<? extends Interceptor> type) {
		return null;
	}

}
