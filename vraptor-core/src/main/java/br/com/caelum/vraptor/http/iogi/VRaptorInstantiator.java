/***
 * Copyright (c) 2009 Caelum - www.caelum.com.br/opensource
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package br.com.caelum.vraptor.http.iogi;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import br.com.caelum.iogi.Instantiator;
import br.com.caelum.iogi.MultiInstantiator;
import br.com.caelum.iogi.ObjectInstantiator;
import br.com.caelum.iogi.collections.ArrayInstantiator;
import br.com.caelum.iogi.collections.ListInstantiator;
import br.com.caelum.iogi.conversion.FallbackConverter;
import br.com.caelum.iogi.conversion.StringConverter;
import br.com.caelum.iogi.parameters.Parameter;
import br.com.caelum.iogi.parameters.Parameters;
import br.com.caelum.iogi.reflection.NewObject;
import br.com.caelum.iogi.reflection.Target;
import br.com.caelum.iogi.spi.DependencyProvider;
import br.com.caelum.iogi.spi.ParameterNamesProvider;
import br.com.caelum.vraptor.converter.ConversionException;
import br.com.caelum.vraptor.converter.Converter;
import br.com.caelum.vraptor.core.Converters;
import br.com.caelum.vraptor.http.InvalidParameterException;
import br.com.caelum.vraptor.validator.Message;
import br.com.caelum.vraptor.validator.SimpleMessage;
import br.com.caelum.vraptor.validator.annotation.ValidationException;

import com.google.common.collect.ImmutableList;

@RequestScoped
public class VRaptorInstantiator implements InstantiatorWithErrors, Instantiator<Object> {
	private MultiInstantiator multiInstantiator;
	private List<Message> errors;
	
	private final Converters converters;
	private final DependencyProvider provider;
	private final ParameterNamesProvider parameterNameProvider;
	private final HttpServletRequest request;

	/** 
	 * @deprecated CDI eyes only
	 */
	protected VRaptorInstantiator() {
		this(null, null, null, null);
	}

	@Inject
	public VRaptorInstantiator(Converters converters, DependencyProvider provider, 
			ParameterNamesProvider parameterNameProvider, HttpServletRequest request) {
		this.converters = converters;
		this.provider = provider;
		this.parameterNameProvider = parameterNameProvider;
		this.request = request;
	}

	@PostConstruct
	public void createInstantiator() {
		List<Instantiator<?>> instantiatorList = ImmutableList.of(
				new RequestAttributeInstantiator(request),
				new VRaptorTypeConverter(converters),
				FallbackConverter.fallbackToNull(new StringConverter()),
				new ArrayAdapter(new ArrayInstantiator(this)),
				new NullDecorator(new ListInstantiator(this)),
				new NullDecorator(new SetInstantiator(this)),
				new DependencyInstantiator(),
				new ObjectInstantiator(this, provider, parameterNameProvider));
		multiInstantiator = new MultiInstantiator(instantiatorList);
	}

	@Override
	public boolean isAbleToInstantiate(Target<?> target) {
		return true;
	}

	@Override
	public Object instantiate(Target<?> target, Parameters parameters, List<Message> errors) {
		this.errors = errors;
		return instantiate(target, parameters);
	}

	@Override
	public Object instantiate(Target<?> target, Parameters parameters) {
		try {
			return multiInstantiator.instantiate(target, parameters);
		} catch(Exception e) {
			handleException(target, e);
			return null;
		}
	}
	private void handleException(Target<?> target, Throwable e) {
		if (e.getClass().isAnnotationPresent(ValidationException.class)) {
			errors.add(new SimpleMessage(target.getName(), e.getLocalizedMessage()));
		} else if (e.getCause() == null) {
			throw new InvalidParameterException("Exception when trying to instantiate " + target, e);
		} else {
			handleException(target, e.getCause());
		}
	}

	private final class DependencyInstantiator implements Instantiator<Object> {

		@Override
		public Object instantiate(Target<?> target, Parameters params) {
			return provider.provide(target);
		}

		@Override
		public boolean isAbleToInstantiate(Target<?> target) {
			return target.getClassType().isInterface() && provider.canProvide(target);
		}

	}

	private final class VRaptorTypeConverter implements Instantiator<Object> {
		private final Converters converters;

		public VRaptorTypeConverter(Converters converters) {
			this.converters = converters;
		}
		
		@Override
		public boolean isAbleToInstantiate(Target<?> target) {
			return !String.class.equals(target.getClassType()) && converters.existsFor(target.getClassType());
		}

		@Override
		public Object instantiate(Target<?> target, Parameters parameters) {
			try {
				Parameter parameter = parameters.namedAfter(target);
				return converterForTarget(target).convert(parameter.getValue(), target.getClassType());
			} catch (ConversionException ex) {
				errors.add(ex.getValidationMessage().withCategory(target.getName()));
			} catch (IllegalStateException e) {
				return setPropertiesAfterConversions(target, parameters);
			}
			return null;
		}

		private Object setPropertiesAfterConversions(Target<?> target, Parameters parameters) {
			List<Parameter> params = parameters.forTarget(target);
			Parameter parameter = findParamFor(params, target);

			Object converted = converterForTarget(target).convert(parameter.getValue(), target.getClassType());

			return new NewObject(this, parameters.focusedOn(target), converted).valueWithPropertiesSet();
		}

		private Parameter findParamFor(List<Parameter> params, Target<?> target) {
			for (Parameter parameter : params) {
				if (parameter.getName().equals(target.getName())) {
					return parameter;
				}
			}
			return null;
		}
		
		@SuppressWarnings("unchecked")
		private Converter<Object> converterForTarget(Target<?> target) {
			return (Converter<Object>) converters.to(target.getClassType());
		}
	}
}
