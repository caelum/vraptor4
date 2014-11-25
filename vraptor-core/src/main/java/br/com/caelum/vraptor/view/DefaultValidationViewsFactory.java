/***
 * Copyright (c) 2009 Caelum - www.caelum.com.br/opensource
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package br.com.caelum.vraptor.view;

import java.lang.reflect.Method;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.View;
import br.com.caelum.vraptor.core.ReflectionProvider;
import br.com.caelum.vraptor.proxy.MethodInvocation;
import br.com.caelum.vraptor.proxy.Proxifier;
import br.com.caelum.vraptor.proxy.SuperMethod;
import br.com.caelum.vraptor.serialization.NoRootSerialization;
import br.com.caelum.vraptor.serialization.Serializer;
import br.com.caelum.vraptor.serialization.SerializerBuilder;
import br.com.caelum.vraptor.validator.Message;
import br.com.caelum.vraptor.validator.ValidationException;

/**
 * Default implementation for ValidationViewsFactory
 *
 * If you want to extend this behavior use:
 * 
 * \@Specializes
 * public class MyValidatorViewsFactory extends DefaultValidationViewsFactory {
 * 		//delegate constructor
 * 		\@Override
 * 		public &lt;T extends View&gt; T instanceFor(Class&lt;T&gt; view, List&lt;Message&gt; errors) {
 * 			//return my own Validation view version or
 * 			return super.instanceFor(view, errors);
 * 		}
 * }
 * @author Lucas Cavalcanti
 * @author Pedro Matiello
 */
@RequestScoped
public class DefaultValidationViewsFactory implements ValidationViewsFactory {

	private final Result result;
	private final Proxifier proxifier;
	private final ReflectionProvider reflectionProvider;

	/** 
	 * @deprecated CDI eyes only
	 */
	protected DefaultValidationViewsFactory() {
		this(null, null, null);
	}

	@Inject
	public DefaultValidationViewsFactory(Result result, Proxifier proxifier, ReflectionProvider reflectionProvider) {
		this.result = result;
		this.proxifier = proxifier;
		this.reflectionProvider = reflectionProvider;
	}

	@Override
	public <T extends View> T instanceFor(final Class<T> view, final List<Message> errors) {
		if (view.equals(EmptyResult.class)) {
			throw new ValidationException(errors);
		}

		return proxifier.proxify(view, throwValidationErrorOnFinalMethods(view, errors, result.use(view)));

	}

	private <T> MethodInvocation<T> throwValidationErrorOnFinalMethods(final Class<T> view, final List<Message> errors,
			final T viewInstance) {
		return new MethodInvocation<T>() {
			@Override
			public Object intercept(T proxy, Method method, Object[] args, SuperMethod superMethod) {
				final Object instance = reflectionProvider.invoke(viewInstance, method, args);
				
				Class type = method.getReturnType();
				if (type == void.class) {
					throw new ValidationException(errors);
				}

				if (view.isAssignableFrom(type)) {
					return proxy;
				}

				if (args.length > 0 && args[0] instanceof Class<?>) {
					return proxifier.proxify((Class<?>) args[0], throwValidationExceptionOnFirstInvocation(errors, instance));
				}

				if (Serializer.class.isAssignableFrom(type)
						|| SerializerBuilder.class.isAssignableFrom(type)
						|| NoRootSerialization.class.isAssignableFrom(type)) {
					return proxifier.proxify(type,
							throwValidationErrorOnFinalMethods(type, errors, type.cast(instance)));
				}
				throw new ResultException("It's not possible to create a validation version of " + method + ". You must provide a Custom Validation version of your class, or inform this corner case to VRaptor developers");
			}

		};
	}

	private <T> MethodInvocation<T> throwValidationExceptionOnFirstInvocation(final List<Message> errors,
			final T instance) {
		return new MethodInvocation<T>() {
			@Override
			public Object intercept(Object proxy, Method method, Object[] args, SuperMethod superMethod) {
				reflectionProvider.invoke(instance, method, args);
				throw new ValidationException(errors);
			}
		};
	}
}
