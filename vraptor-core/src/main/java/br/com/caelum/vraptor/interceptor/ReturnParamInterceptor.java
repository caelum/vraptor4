package br.com.caelum.vraptor.interceptor;

import javax.inject.Inject;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.Intercepts;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.core.MethodInfo;
import br.com.caelum.vraptor.http.ParameterNameProvider;
import br.com.caelum.vraptor4.controller.ControllerMethod;

@Intercepts(
	before=ExecuteMethodInterceptor.class,
	after=ParametersInstantiatorInterceptor.class
)
public class ReturnParamInterceptor {

	@Inject private MethodInfo info;
	@Inject private Result result;
	@Inject private ParameterNameProvider nameProvider;

	public void intercept(InterceptorStack stack, ControllerMethod method,
			Object instance) throws InterceptionException {

		Object[] parameters = info.getParameters();
		String[] names = nameProvider.parameterNamesFor(method.getMethod());

		for(int i=0; i< names.length; i++) {
			result.include(names[i], parameters[i]);
		}
		stack.next(method, instance);
	}
}