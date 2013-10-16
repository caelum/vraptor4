package br.com.caelum.vraptor.interceptor;

import static java.lang.String.format;
import br.com.caelum.vraptor.AfterCall;
import br.com.caelum.vraptor.AroundCall;
import br.com.caelum.vraptor.BeforeCall;
import br.com.caelum.vraptor.InterceptionException;

public class NoInterceptMethodsValidationRule implements ValidationRule {

	@Override
	public void validate(Class<?> originalType) {

		boolean hasAfterMethod = false;  // TODO handle method efficiently
		boolean hasAroundMethod = false; // TODO handle method efficiently
		boolean hasBeforeMethod = false; // TODO handle method efficiently

		if (!hasAfterMethod && !hasAroundMethod && !hasBeforeMethod) {

			throw new InterceptionException(format("Interceptor %s must "
				+ "declare at least one method whith @%s, @%s or @%s annotation",
				originalType.getCanonicalName(), AfterCall.class.getSimpleName(),
				AroundCall.class.getSimpleName(), BeforeCall.class.getSimpleName()));
		}
	}
}