package br.com.caelum.vraptor.interceptor;

import static java.lang.String.format;

import java.lang.reflect.Method;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import net.vidageek.mirror.list.dsl.MirrorList;
import br.com.caelum.vraptor.Accepts;
import br.com.caelum.vraptor.InterceptionException;

@Dependent
public class AcceptsNeedReturnBooleanValidationRule implements ValidationRule {

	private final StepInvoker invoker;
	
	/** @deprecated CDI eyes only */
	protected AcceptsNeedReturnBooleanValidationRule() {
		this(null);
	}

	@Inject
	public AcceptsNeedReturnBooleanValidationRule(StepInvoker invoker) {
		this.invoker = invoker;
	}

	@Override
	public void validate(Class<?> originalType, MirrorList<Method> methods) {
		Method accepts = invoker.findMethod(methods, Accepts.class, originalType);

		if (accepts != null && !isBooleanReturn(accepts.getReturnType())) {
			throw new InterceptionException(format("@%s method must return boolean", 
					Accepts.class.getSimpleName()));
		}
	}

	private boolean isBooleanReturn(Class<?> returnType) {
		return returnType.equals(Boolean.class) || returnType.equals(boolean.class);
	}
}