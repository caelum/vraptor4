package br.com.caelum.vraptor.interceptor;

import java.lang.reflect.Method;

import javax.inject.Inject;

import br.com.caelum.vraptor.Intercepts;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.core.MethodInfo;
import br.com.caelum.vraptor.http.ParameterNameProvider;
import br.com.caelum.vraptor4.BeforeCall;
import br.com.caelum.vraptor4.controller.ControllerMethod;

@Intercepts(
	after=ParametersInstantiatorInterceptor.class
)
public class ReturnParamInterceptor {

	private MethodInfo info;
	private Result result;
	private ParameterNameProvider nameProvider;
	private ControllerMethod controllerMethod;

	@Deprecated // CDI eyes only
	public ReturnParamInterceptor() {}

	@Inject
	public ReturnParamInterceptor(MethodInfo info,
			Result result, ParameterNameProvider nameProvider,
			ControllerMethod controllerMethod) {

		this.info = info;
		this.result = result;
		this.nameProvider = nameProvider;
		this.controllerMethod = controllerMethod;
	}

	@BeforeCall
	public void intercept() {

		Object[] parameters = info.getParameters();
		Method method = controllerMethod.getMethod();
		String[] names = nameProvider.parameterNamesFor(method);

		for(int i=0; i< names.length; i++) {
			result.include(names[i], parameters[i]);
		}
	}

}