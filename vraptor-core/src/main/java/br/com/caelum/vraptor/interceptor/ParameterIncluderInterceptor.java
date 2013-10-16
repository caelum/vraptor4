package br.com.caelum.vraptor.interceptor;

import java.lang.reflect.Method;

import javax.inject.Inject;

import br.com.caelum.vraptor.BeforeCall;
import br.com.caelum.vraptor.Intercepts;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.controller.ControllerMethod;
import br.com.caelum.vraptor.core.MethodInfo;
import br.com.caelum.vraptor.http.ParameterNameProvider;

/**
 * Interceptor that includes all the parameters on the view of
 * a method annotated with {@link IncludeParameters} annotation
 *
 * @author Rodrigo Turini
 * @since 4.0
 */
@Intercepts(
	after=ParametersInstantiatorInterceptor.class
)
@AcceptsWithAnnotations(IncludeParameters.class)
public class ParameterIncluderInterceptor {

	private final MethodInfo info;
	private final Result result;
	private final ParameterNameProvider nameProvider;
	private final ControllerMethod controllerMethod;

	/** @deprecated CDI eyes only */
	protected ParameterIncluderInterceptor() {
		this(null, null, null, null);
	}

	@Inject
	public ParameterIncluderInterceptor(MethodInfo info, Result result, ParameterNameProvider nameProvider,
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