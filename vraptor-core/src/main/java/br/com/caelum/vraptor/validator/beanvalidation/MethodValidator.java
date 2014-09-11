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
import javax.enterprise.inject.Instance;
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
import br.com.caelum.vraptor.events.MethodReady;
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

	private final Instance<Locale> locale;
	private final MessageInterpolator interpolator;
	private final javax.validation.Validator bvalidator;

	/**
	 * @deprecated CDI eyes only
	 */
	protected MethodValidator() {
		this(null, null, null);
	}

	@Inject
	public MethodValidator(Instance<Locale> locale, MessageInterpolator interpolator, javax.validation.Validator bvalidator) {
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
		if(bean == null) {
			return false;
		}
		MethodDescriptor descriptor = bean.getConstraintsForMethod(method.getName(), method.getParameterTypes());
		return descriptor != null && descriptor.hasConstrainedParameters();
	}

	public void validate(@Observes MethodReady event, ControllerInstance controllerInstance, MethodInfo methodInfo, 
			Validator validator) {
		ControllerMethod method = event.getControllerMethod();

		if (hasConstraints(method)) {
			Set<ConstraintViolation<Object>> violations = bvalidator.forExecutables().validateParameters(
					controllerInstance.getController(), method.getMethod(), methodInfo.getParametersValues());

			logger.debug("there are {} constraint violations at method {}.", violations.size(), method);

			for (ConstraintViolation<Object> v : violations) {
				String category = extractCategory(methodInfo.getValuedParameters(), v);
				String msg = extractInternacionalizedMessage(v);
				validator.add(new SimpleMessage(category, msg));
				logger.debug("added message {}={} for contraint violation", category, msg);
			}
		}
	}

	/**
	 * Returns the category for this constraint violation. By default, the category returned
	 * is the full path for property. You can override this method if you prefer another strategy.
	 */
	protected String extractCategory(ValuedParameter[] params, ConstraintViolation<Object> violation) {
		Iterator<Node> path = violation.getPropertyPath().iterator();
		Node method = path.next();
		logger.debug("Constraint violation on method {}: {}", method, violation);

		StringBuilder cat = new StringBuilder();
		cat.append(params[path.next().as(ParameterNode.class).getParameterIndex()].getName());// parameter name

		while (path.hasNext()) {
			cat.append(".").append(path.next());
		}

		return cat.toString();
	}

	/**
	 * Returns the internacionalized message for this constraint violation.
	 */
	protected String extractInternacionalizedMessage(ConstraintViolation<Object> v) {
		return interpolator.interpolate(v.getMessageTemplate(), new BeanValidatorContext(v), locale.get());
	}
}
