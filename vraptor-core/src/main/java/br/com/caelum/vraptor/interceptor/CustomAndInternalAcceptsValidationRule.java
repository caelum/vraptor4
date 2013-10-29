package br.com.caelum.vraptor.interceptor;

import static br.com.caelum.vraptor.interceptor.CustomAcceptsVerifier.getCustomAcceptsAnnotations;
import static com.google.common.base.Preconditions.checkState;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;

import javax.inject.Inject;

import net.vidageek.mirror.list.dsl.MirrorList;
import br.com.caelum.vraptor.Accepts;

public class CustomAndInternalAcceptsValidationRule implements ValidationRule {

	private final StepInvoker invoker;

	/**
	 * @deprecated CDI eyes only
	 */
	protected CustomAndInternalAcceptsValidationRule() {
		this(null);
	}

	@Inject
	public CustomAndInternalAcceptsValidationRule(StepInvoker invoker) {
		this.invoker = invoker;
	}

	@Override
	public void validate(Class<?> originalType, MirrorList<Method> methods) {

		Method accepts = invoker.findMethod(methods, Accepts.class, originalType);
		List<Annotation> constraints = getCustomAcceptsAnnotations(originalType);

		checkState(accepts == null || constraints.isEmpty(), "Interceptor "
			+ "%s must declare internal accepts or custom, not both.", originalType);
	}
}