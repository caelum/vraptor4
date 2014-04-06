package br.com.caelum.vraptor.interceptor;

import static com.google.common.base.Preconditions.checkState;

import java.lang.reflect.Method;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import net.vidageek.mirror.list.dsl.MirrorList;
import br.com.caelum.vraptor.Intercepts;

@Dependent
public class InterceptorValidator {

	private @Inject @Any Instance<ValidationRule> validationRules;
	private @Inject @Any StepInvoker stepInvoker;

	public void validate(Class<?> originalType) {
		boolean implementsInterceptor = Interceptor.class.isAssignableFrom(originalType);
		boolean containsIntercepts = originalType.isAnnotationPresent(Intercepts.class);

		checkState(implementsInterceptor || containsIntercepts, 
				"Annotation @Intercepts found in %s, but it is not an Interceptor.", originalType);

		applyNewInterceptorValidationRules(originalType, implementsInterceptor);
	}

	private void applyNewInterceptorValidationRules(Class<?> originalType,
			boolean implementsInterceptor) {

		if (!implementsInterceptor) {
			MirrorList<Method> allMethods = stepInvoker.findAllMethods(originalType);
			for (ValidationRule validationRule : this.validationRules) {
				validationRule.validate(originalType, allMethods);
			}
		}
	}
}