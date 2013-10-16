package br.com.caelum.vraptor.interceptor;

import static java.lang.String.format;

import java.lang.reflect.Method;

import javax.enterprise.context.ApplicationScoped;

import br.com.caelum.vraptor.Accepts;
import br.com.caelum.vraptor.InterceptionException;

@ApplicationScoped
public class AcceptsNeedReturnBooleanValidationRule implements ValidationRule {

	@Override
	public void validate(Class<?> originalType) {

		Method acceptsMethod = null; // TODO handle method efficiently

		if (!acceptsMethod.getReturnType().equals(Boolean.class)
				&& !acceptsMethod.getReturnType().equals(boolean.class)) {
			throw new InterceptionException(format("@%s method must return "
				+ "	boolean", Accepts.class.getSimpleName()));
		}
	}
}