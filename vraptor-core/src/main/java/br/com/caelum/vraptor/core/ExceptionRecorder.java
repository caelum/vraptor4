/***
 * Copyright (c) 2009 Caelum - www.caelum.com.br/opensource
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package br.com.caelum.vraptor.core;

import java.lang.reflect.Method;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.inject.Vetoed;

import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.proxy.MethodInvocation;
import br.com.caelum.vraptor.proxy.Proxifier;
import br.com.caelum.vraptor.proxy.SuperMethod;

/**
 * Create proxies to store state of exception mapping.
 *
 * <p>This class is a part of Exception Handling Feature.</p>
 *
 * @author Otávio Scherer Garcia
 * @author Lucas Cavalcanti
 * @since 3.2
 */
@Vetoed
public class ExceptionRecorder<T> implements MethodInvocation<T> {

	private final List<ExceptionRecorderParameter> parameters = new ArrayList<>();
	private final Proxifier proxifier;
	private final ReflectionProvider reflectionProvider;

	public ExceptionRecorder(Proxifier proxifier, ReflectionProvider reflectionProvider) {
		this.proxifier = proxifier;
		this.reflectionProvider = reflectionProvider;
	}

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Object intercept(T proxy, Method method, Object[] args, SuperMethod superMethod) {
		parameters.add(new ExceptionRecorderParameter(args, method));

		if (void.class.equals(method.getReturnType())) {
			return null;
		}

		return proxifier.proxify(findReturnType(method, args), (MethodInvocation) this);
	}

	private Class<?> findReturnType(Method method, Object[] args) {
		if (method.getGenericReturnType() instanceof TypeVariable) {
			if (args[0] instanceof Class) {
				return (Class<?>) args[0];
			}
			return args[0].getClass();
		}

		return method.getReturnType();
	}

	public void replay(Result result) {
		Object current = result;
		for (ExceptionRecorderParameter p : parameters) {
			current = reflectionProvider.invoke(current, p.getMethod(), p.getArgs());
		}
	}
}
