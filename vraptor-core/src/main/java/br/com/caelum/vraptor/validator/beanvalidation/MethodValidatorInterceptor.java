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

import java.util.Iterator;
import java.util.Locale;
import java.util.Set;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.MessageInterpolator;
import javax.validation.Path.Node;
import javax.validation.Path.ParameterNode;
import javax.validation.metadata.BeanDescriptor;
import javax.validation.metadata.MethodDescriptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.Intercepts;
import br.com.caelum.vraptor.controller.ControllerMethod;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.core.MethodInfo;
import br.com.caelum.vraptor.http.Parameter;
import br.com.caelum.vraptor.http.ParameterNameProvider;
import br.com.caelum.vraptor.interceptor.ExecuteMethodInterceptor;
import br.com.caelum.vraptor.interceptor.Interceptor;
import br.com.caelum.vraptor.interceptor.ParametersInstantiatorInterceptor;
import br.com.caelum.vraptor.validator.SimpleMessage;
import br.com.caelum.vraptor.validator.Validator;

import com.google.common.base.Joiner;

/**
 * Validate method parameters using Bean Validation. The method will be
 * intercepted if any parameter contains a Bean Validation annotation.
 *
 * @author Otávio Scherer Garcia
 * @since 3.5.1
 */
@RequestScoped
@Intercepts(before = ExecuteMethodInterceptor.class, after = ParametersInstantiatorInterceptor.class)
public class MethodValidatorInterceptor implements Interceptor {

	private static final Logger logger = LoggerFactory.getLogger(MethodValidatorInterceptor.class);
	
	private final Locale locale;
	private final MessageInterpolator interpolator;
	private final MethodInfo methodInfo;
	private final Validator validator;
	private final ParameterNameProvider parameterNameProvider;
	private final javax.validation.Validator bvalidator;

	/** 
	 * @deprecated CDI eyes only
	 */
	protected MethodValidatorInterceptor() {
		this(null, null, null, null, null, null);
	}

	@Inject
	public MethodValidatorInterceptor(Locale locale, MessageInterpolator interpolator, Validator validator,
			MethodInfo methodInfo, javax.validation.Validator bvalidator, ParameterNameProvider parameterNameProvider) {
		this.locale = locale;
		this.interpolator = interpolator;
		this.validator = validator;
		this.methodInfo = methodInfo;
		this.bvalidator = bvalidator;
		this.parameterNameProvider = parameterNameProvider;
	}

	/**
	 * Only accepts if method isn't parameterless and have at least one constraint.
	 */
	@Override
	public boolean accepts(ControllerMethod method) {
		if (method.getMethod().getParameterTypes().length == 0) {
			logger.debug("method {} has no parameters, skipping", method);
			return false;
		}
		
		BeanDescriptor bean = bvalidator.getConstraintsForClass(method.getController().getType());
		MethodDescriptor descriptor = bean.getConstraintsForMethod(method.getMethod().getName(), method.getMethod()
				.getParameterTypes());
		
		return descriptor != null && descriptor.hasConstrainedParameters();
	}

	@Override
	public void intercept(InterceptorStack stack, ControllerMethod method, Object controllerInstance)
			throws InterceptionException {

		Set<ConstraintViolation<Object>> violations = bvalidator.forExecutables()
				.validateParameters(controllerInstance, method.getMethod(), methodInfo.getParameters());
		logger.debug("there are {} violations at method {}.", violations.size(), method);

		Parameter[] params = violations.isEmpty() ? new Parameter[0] : parameterNameProvider.parametersFor(method.getMethod());

		for (ConstraintViolation<Object> v : violations) {
			BeanValidatorContext ctx = new BeanValidatorContext(v);
			String msg = interpolator.interpolate(v.getMessageTemplate(), ctx, locale);
			String category = extractCategory(params, v);
			validator.add(new SimpleMessage(category, msg));
			
			logger.debug("added message {}={} for contraint violation", category, msg);
		}

		stack.next(method, controllerInstance);
	}

	/**
	 * Returns the category for this constraint violation. By default, the category returned
	 * is the name of method with full path for property. You can override this method to
	 * change this behaviour.
	 */
	protected String extractCategory(Parameter[] params, ConstraintViolation<Object> v) {
		Iterator<Node> property = v.getPropertyPath().iterator();
		property.next();
		ParameterNode parameterNode = property.next().as(ParameterNode.class);

		int index = parameterNode.getParameterIndex();
		return Joiner.on(".").join(v.getPropertyPath())
				.replace("arg" + parameterNode.getParameterIndex(), params[index].getName());
	}
}
