package br.com.caelum.vraptor.interceptor;

import static java.lang.String.format;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;

import br.com.caelum.vraptor.Intercepts;
import br.com.caelum.vraptor.VRaptorException;

@ApplicationScoped
public class InterceptorValidator {

	private @Any Instance<ValidationRule> validationRules;

	public void validate(Class<?> originalType) {

		boolean notOldInterceptor = !Interceptor.class.isAssignableFrom(originalType);
		validateIfItsAnValidInterceptor(originalType, notOldInterceptor);

		if (notOldInterceptor) {
			for (ValidationRule validationRule : this.validationRules) {
				validationRule.validate(originalType);
			}
		}
	}

	private void validateIfItsAnValidInterceptor(
			Class<?> originalType, boolean isntInterceptor) {

		if (isntInterceptor || !originalType.isAnnotationPresent(Intercepts.class)) {

			throw new VRaptorException(format("Annotation @%s found in %s, "
				+ "but it is neither an Interceptor nor an InterceptorSequence.",
				Intercepts.class.getSimpleName(), originalType));
		}
	}
}