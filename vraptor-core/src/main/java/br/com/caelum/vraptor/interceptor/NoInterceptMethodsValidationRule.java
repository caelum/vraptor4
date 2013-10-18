package br.com.caelum.vraptor.interceptor;

import static java.lang.String.format;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import net.vidageek.mirror.list.dsl.MirrorList;
import br.com.caelum.vraptor.AfterCall;
import br.com.caelum.vraptor.AroundCall;
import br.com.caelum.vraptor.BeforeCall;
import br.com.caelum.vraptor.InterceptionException;

@ApplicationScoped
public class NoInterceptMethodsValidationRule implements ValidationRule {

	private StepInvoker stepInvoker;

	@Inject
	public NoInterceptMethodsValidationRule(StepInvoker stepInvoker) {
		this.stepInvoker = stepInvoker;
	}

	@Override
	public void validate(Class<?> originalType, MirrorList<Method> methods) {

		boolean hasAfterMethod = notNull(AfterCall.class, originalType, methods);
		boolean hasAroundMethod = notNull(AroundCall.class, originalType, methods);
		boolean hasBeforeMethod = notNull(BeforeCall.class, originalType, methods);

		if (!hasAfterMethod && !hasAroundMethod && !hasBeforeMethod) {

			throw new InterceptionException(format("Interceptor %s must "
				+ "declare at least one method whith @AfterCall, @AroundCall "
				+ "or @BeforeCall annotation", originalType.getCanonicalName()));
		}
	}

	private boolean notNull(Class<? extends Annotation> step,
			Class<?> originalType, MirrorList<Method> methods) {

		return stepInvoker.findMethod(methods, step, originalType) != null;
	}
}