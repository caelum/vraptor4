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
package br.com.caelum.vraptor;

import static org.hamcrest.Matchers.equalTo;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.ResourceBundle;

import com.thoughtworks.xstream.InitializationException;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import br.com.caelum.vraptor.converter.ConversionException;
import br.com.caelum.vraptor.converter.ConversionMessage;
import br.com.caelum.vraptor.http.route.Route;
import br.com.caelum.vraptor.validator.Message;

public final class VRaptorMatchers {
	private VRaptorMatchers(){
		throw new InitializationException("Not allowed to initialize");
	}

	public static Matcher<Collection<?>> hasOneCopyOf(final Object item) {
		return new TypeSafeMatcher<Collection<?>>(){

			@Override
			public void describeTo(Description description) {
				description.appendText("a collection containing one copy of").appendValue(item);
			}

			@Override
			protected void describeMismatchSafely(Collection<?> sut, Description mismatchDescription) {
				mismatchDescription
					.appendText("a collection containing")
					.appendValue(Collections.frequency(sut, item))
					.appendText("copies");
			}

			@Override
			protected boolean matchesSafely(Collection<?> sut) {
				return Collections.frequency(sut, item) == 1;
			}

		};
	}

	public static Matcher<Route> canHandle(final Class<?> type, final Method method) {
		return new TypeSafeMatcher<Route>() {

			@Override
			protected void describeMismatchSafely(Route item, Description mismatchDescription) {
				mismatchDescription.appendValue(item);
			}

			@Override
			protected boolean matchesSafely(Route item) {
				return item.canHandle(type, method);
			}

			@Override
			public void describeTo(Description description) {
				description.appendText("a route that can handle class ")
						.appendValue(type).appendText(" method ").appendValue(method);
			}
		};
	}

	public static Matcher<Message> hasMessage(String message) {
		final Matcher<? super String> delegate = equalTo(message);
		return new TypeSafeMatcher<Message>() {
	
			@Override
			public void describeTo(Description description) {
				delegate.describeTo(description);
			}
	
			@Override
			protected boolean matchesSafely(Message message) {
				message.setBundle(ResourceBundle.getBundle("messages"));
				return delegate.matches(message.getMessage());
			}
		};
	}

	public static TypeSafeMatcher<Exception> hasConversionException(String message) {
		final Matcher<? extends String> delegate = equalTo(message);
		return new TypeSafeMatcher<Exception>() {
			@Override
			protected boolean matchesSafely(Exception item) {
				if (item instanceof ConversionException) {
					ConversionMessage message = ((ConversionException) item).getValidationMessage();
					message.setBundle(ResourceBundle.getBundle("messages"));
					return delegate.matches(message.getMessage());
				}

				return false;
			}

			@Override
			public void describeTo(Description description) {
				delegate.describeTo(description);
			}
		};
	}
}
