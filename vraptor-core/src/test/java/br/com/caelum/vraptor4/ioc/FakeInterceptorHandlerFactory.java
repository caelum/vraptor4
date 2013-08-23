package br.com.caelum.vraptor4.ioc;

import javax.enterprise.context.ApplicationScoped;

import br.com.caelum.vraptor4.core.InterceptorHandler;
import br.com.caelum.vraptor4.core.InterceptorHandlerFactory;

@ApplicationScoped
public class FakeInterceptorHandlerFactory implements InterceptorHandlerFactory{

	public InterceptorHandler handlerFor(Class<?> type) {
		return null;
	}

}
