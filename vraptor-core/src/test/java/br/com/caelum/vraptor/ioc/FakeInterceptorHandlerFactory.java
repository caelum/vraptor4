package br.com.caelum.vraptor.ioc;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;

import br.com.caelum.vraptor.core.InterceptorHandler;
import br.com.caelum.vraptor.core.InterceptorHandlerFactory;

@ApplicationScoped
@Alternative
public class FakeInterceptorHandlerFactory implements InterceptorHandlerFactory{

	@Override
	public InterceptorHandler handlerFor(Class<?> type) {
		return null;
	}
}