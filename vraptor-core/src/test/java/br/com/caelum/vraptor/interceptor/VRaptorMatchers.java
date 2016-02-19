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
package br.com.caelum.vraptor.interceptor;

import java.lang.reflect.Method;

import com.thoughtworks.xstream.InitializationException;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import br.com.caelum.vraptor.controller.BeanClass;
import br.com.caelum.vraptor.controller.ControllerMethod;
import br.com.caelum.vraptor.validator.SimpleMessage;

/**
 * Useful matchers to use while mocking and hamcresting tests with internal
 * vraptor information.
 *
 * @author Guilherme Silveira
 */
public final class VRaptorMatchers {
	private VRaptorMatchers(){
		throw new InitializationException("Not allowed to initialize");
	}
	public static TypeSafeMatcher<ControllerMethod> controllerMethod(final Method method) {
		return new TypeSafeMatcher<ControllerMethod>() {

			@Override
			public boolean matchesSafely(ControllerMethod other) {
				return other.getMethod().equals(method);
			}

			@Override
			public void describeTo(Description description) {
				description.appendText(" an instance of a controller method for method " + method.getName() + " declared at " + method.getDeclaringClass().getName());
			}

			@Override
			protected void describeMismatchSafely(ControllerMethod item, Description mismatchDescription) {
				mismatchDescription.appendText(" an instance of a controller method for method " + item.getMethod().getName() + " declared at " + item.getMethod().getDeclaringClass().getName());
			}

		};
	}

	public static Matcher<BeanClass> controller(final Class<?> type) {
		return new BaseMatcher<BeanClass>() {

			@Override
			public boolean matches(Object item) {
				if (!(item instanceof BeanClass)) {
					return false;
				}
				BeanClass other = (BeanClass) item;
				return other.getType().equals(type);
			}

			@Override
			public void describeTo(Description description) {
				description.appendText(" controller for " + type.getName());
			}

		};
	}

	public static Matcher<SimpleMessage> error(final String category, final String message) {
		return new TypeSafeMatcher<SimpleMessage>() {

			@Override
			protected void describeMismatchSafely(SimpleMessage item, Description mismatchDescription) {
				mismatchDescription.appendText(" validation message='" +item.getMessage() + "', category = '"+item.getCategory()+"'");
			}

			@Override
			protected boolean matchesSafely(SimpleMessage m) {
				return message.equals(m.getMessage()) && category.equals(m.getCategory());
			}

			@Override
			public void describeTo(Description description) {
				description.appendText(" validation message='" +message + "', category = '"+category+"'");
			}

		};
	}

}
