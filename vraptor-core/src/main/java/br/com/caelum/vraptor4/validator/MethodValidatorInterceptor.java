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
package br.com.caelum.vraptor4.validator;

import java.util.Locale;
import java.util.Set;

import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.MessageInterpolator;
import javax.validation.metadata.BeanDescriptor;
import javax.validation.metadata.MethodDescriptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor4.InterceptionException;
import br.com.caelum.vraptor4.Intercepts;
import br.com.caelum.vraptor4.Validator;
import br.com.caelum.vraptor4.core.InterceptorStack;
import br.com.caelum.vraptor4.core.Localization;
import br.com.caelum.vraptor4.core.MethodInfo;
import br.com.caelum.vraptor4.interceptor.ExecuteMethodInterceptor;
import br.com.caelum.vraptor4.interceptor.Interceptor;
import br.com.caelum.vraptor4.interceptor.ParametersInstantiatorInterceptor;
import br.com.caelum.vraptor4.ioc.RequestScoped;
import br.com.caelum.vraptor4.restfulie.controller.ControllerMethod;

/**
 * Validate method parameters using Bean Validation 1.1. The method will be
 * intercepted if any parameter contains Bean Validation annotations. This
 * component is enabled only if you have any Bean Validation provider that
 * implements method validation.
 * 
 * @author Otávio Scherer Garcia
 * @since 3.5.1-SNAPSHOT
 */
@RequestScoped
@Intercepts(before = ExecuteMethodInterceptor.class, after = ParametersInstantiatorInterceptor.class)
public class MethodValidatorInterceptor implements Interceptor {

	private static final Logger logger = LoggerFactory.getLogger(MethodValidatorInterceptor.class);

	private javax.validation.Validator methodValidator;
	private Localization localization;
	private MessageInterpolator interpolator;
	private MethodInfo methodInfo;
	private Validator validator;

	@Deprecated
	public MethodValidatorInterceptor() {}

	@Inject
	public MethodValidatorInterceptor(Localization localization, MessageInterpolator interpolator, Validator validator,
			MethodInfo methodInfo, javax.validation.Validator methodValidator) {
		this.localization = localization;
		this.interpolator = interpolator;
		this.validator = validator;
		this.methodInfo = methodInfo;
		this.methodValidator = methodValidator;
	}

	@Override
	public boolean accepts(ControllerMethod method) {
		BeanDescriptor bean = methodValidator.getConstraintsForClass(method.getController().getType());
		MethodDescriptor descriptor = bean.getConstraintsForMethod(method.getMethod().getName(), method.getMethod()
				.getParameterTypes());
		return descriptor != null && descriptor.hasConstrainedParameters();
	}

	@Override
	public void intercept(InterceptorStack stack, ControllerMethod method, Object controllerInstance)
			throws InterceptionException {

		Set<ConstraintViolation<Object>> violations = methodValidator.forExecutables().validateParameters(
				controllerInstance, method.getMethod(), methodInfo.getParameters());
		logger.debug("there are {} violations at method {}.", violations.size(), method);

		for (ConstraintViolation<Object> violation : violations) {
			BeanValidatorContext ctx = BeanValidatorContext.of(violation);
			String msg = interpolator.interpolate(violation.getMessageTemplate(), ctx, getLocale());

			validator.add(new ValidationMessage(msg, violation.getPropertyPath().toString()));
			logger.debug("added message {} to validation of bean {}", msg, violation.getRootBean());
		}

		stack.next(method, controllerInstance);
	}

	private Locale getLocale() {
		return localization.getLocale() == null ? Locale.getDefault() : localization.getLocale();
	}
}
