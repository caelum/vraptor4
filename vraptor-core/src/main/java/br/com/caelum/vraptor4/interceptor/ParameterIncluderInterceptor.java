package br.com.caelum.vraptor4.interceptor;

import java.lang.reflect.Method;

import javax.inject.Inject;

import br.com.caelum.vraptor4.BeforeCall;
import br.com.caelum.vraptor4.Intercepts;
import br.com.caelum.vraptor4.Result;
import br.com.caelum.vraptor4.core.MethodInfo;
import br.com.caelum.vraptor4.http.ParameterNameProvider;
import br.com.caelum.vraptor4.restfulie.controller.ControllerMethod;

/**
 * An interceptor that auto include all controller method
 * parameters to view
 *
 * @author Rodrigo Turini
 * @since 4.0
 */
@Intercepts(
	after=ParametersInstantiatorInterceptor.class
)
public class ParameterIncluderInterceptor {

	private MethodInfo info;
	private Result result;
	private ParameterNameProvider nameProvider;
	private ControllerMethod controllerMethod;

	@Deprecated // CDI eyes only
	public ParameterIncluderInterceptor() {}

	@Inject
	public ParameterIncluderInterceptor(MethodInfo info,
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