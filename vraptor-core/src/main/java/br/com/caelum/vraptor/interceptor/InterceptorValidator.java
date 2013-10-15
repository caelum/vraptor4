package br.com.caelum.vraptor.interceptor;

import static java.lang.String.format;

import javax.enterprise.context.ApplicationScoped;

import br.com.caelum.vraptor.Intercepts;
import br.com.caelum.vraptor.VRaptorException;

@ApplicationScoped
public class InterceptorValidator {

	public void validate(Class<?> originalType) {

		if (!Interceptor.class.isAssignableFrom(originalType)
				|| !originalType.isAnnotationPresent(Intercepts.class)) {

			throw new VRaptorException(format("Annotation @%s found in %s, "
				+ "but it is neither an Interceptor nor an InterceptorSequence.",
				Intercepts.class.getSimpleName(), originalType));
		}
	}
}