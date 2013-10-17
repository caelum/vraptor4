package br.com.caelum.vraptor.interceptor;

import static java.lang.String.format;

import java.lang.reflect.Method;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;

import br.com.caelum.vraptor.Intercepts;
import br.com.caelum.vraptor.VRaptorException;

@ApplicationScoped
public class InterceptorValidator {

	private @Any Instance<ValidationRule> validationRules;
	private @Any StepInvoker stepInvoker;

	public void validate(Class<?> originalType) {

		boolean implementsInterceptor = Interceptor.class.isAssignableFrom(originalType);
		boolean containsIntercepts = originalType.isAnnotationPresent(Intercepts.class);

		if (implementsInterceptor || containsIntercepts) {
			applyNewInterceptorValidationRules(originalType, implementsInterceptor);
		} else {
			throw new VRaptorException(format("Annotation @%s found in %s, "
				+ "but it is neither an Interceptor nor an InterceptorSequence.",
				Intercepts.class.getSimpleName(), originalType));
		}
	}

	private void applyNewInterceptorValidationRules(Class<?> originalType,
			boolean implementsInterceptor) {

		if (!implementsInterceptor) {
			List<Method> allMethods = stepInvoker.findAllMethods(originalType);
			for (ValidationRule validationRule : this.validationRules) {
				validationRule.validate(originalType, allMethods);
			}
		}
	}
}