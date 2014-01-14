/***
 * Copyright (c) 2009 Caelum - www.caelum.com.br/opensource All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package br.com.caelum.vraptor.validator.beanvalidation;

import static org.slf4j.LoggerFactory.getLogger;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.MessageInterpolator;
import javax.validation.Path.Node;
import javax.validation.Path.ParameterNode;
import javax.validation.metadata.BeanDescriptor;
import javax.validation.metadata.MethodDescriptor;

import org.slf4j.Logger;

import br.com.caelum.vraptor.controller.ControllerInstance;
import br.com.caelum.vraptor.controller.ControllerMethod;
import br.com.caelum.vraptor.core.MethodInfo;
import br.com.caelum.vraptor.events.ReadyToExecuteMethod;
import br.com.caelum.vraptor.http.ValuedParameter;
import br.com.caelum.vraptor.validator.SimpleMessage;
import br.com.caelum.vraptor.validator.Validator;

/**
 * Validate method parameters using Bean Validation. The method will
 * be validated if any parameter contains a Bean Validation annotation.
 *
 * @author Otávio Scherer Garcia
 * @author Rodrigo Turini
 * @since 3.5.1
 */
@ApplicationScoped
public class MethodValidator {

	private static final Logger logger = getLogger(MethodValidator.class);

	private final Locale locale;
	private final MessageInterpolator interpolator;
	private final javax.validation.Validator bvalidator;

	/**
	 * @deprecated CDI eyes only
	 */
	protected MethodValidator() {
		this(null, null, null);
	}

	@Inject
	public MethodValidator(Locale locale, MessageInterpolator interpolator, javax.validation.Validator bvalidator) {
		this.locale = locale;
		this.interpolator = interpolator;
		this.bvalidator = bvalidator;
	}

	/**
	 * Only accepts if method isn't parameterless and have at least one constraint.
	 */
	private boolean hasConstraints(ControllerMethod controllerMethod) {
		Method method = controllerMethod.getMethod();
		if (method.getParameterTypes().length == 0) {
			logger.debug("method {} has no parameters, skipping", controllerMethod);
			return false;
		}

		BeanDescriptor bean = bvalidator.getConstraintsForClass(controllerMethod.getController().getType());
		MethodDescriptor descriptor = bean.getConstraintsForMethod(method.getName(), method.getParameterTypes());
		return descriptor != null && descriptor.hasConstrainedParameters();
	}

	public void validate(@Observes ReadyToExecuteMethod event, ControllerInstance controllerInstance,
			MethodInfo methodInfo, Validator validator) {
		ControllerMethod controllerMethod = event.getControllerMethod();

		if (hasConstraints(controllerMethod)) {
			Set<ConstraintViolation<Object>> violations = bvalidator.forExecutables().validateParameters(
					controllerInstance.getController(), controllerMethod.getMethod(), methodInfo.getParametersValues());

			logger.debug("there are {} violations at method {}.", violations.size(), controllerMethod);

			for (ConstraintViolation<Object> v : violations) {
				BeanValidatorContext ctx = new BeanValidatorContext(v);
				String msg = interpolator.interpolate(v.getMessageTemplate(), ctx, locale);
				String category = extractCategory(methodInfo.getValuedParameters(), v);
				validator.add(new SimpleMessage(category, msg));
				logger.debug("added message {}={} for contraint violation", category, msg);
			}
		}
	}

	/**
	 * Returns the category for this constraint violation. By default, the category returned
	 * is the name of method with full path for property. You can override this method to
	 * change this behaviour.
	 */
	protected String extractCategory(ValuedParameter[] params, ConstraintViolation<Object> violation) {
		Iterator<Node> path = violation.getPropertyPath().iterator();

		StringBuilder cat = new StringBuilder();
		cat.append(path.next()).append("."); // method name
		cat.append(params[path.next().as(ParameterNode.class).getParameterIndex()].getName());// parameter name

		while (path.hasNext()) {
			cat.append(".").append(path.next());
		}

		return cat.toString();
	}
}