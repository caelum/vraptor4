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
package br.com.caelum.vraptor.http;

import java.lang.reflect.AccessibleObject;

import javax.annotation.Priority;
import javax.enterprise.context.ApplicationScoped;
import javax.interceptor.Interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.paranamer.AnnotationParanamer;
import com.thoughtworks.paranamer.BytecodeReadingParanamer;
import com.thoughtworks.paranamer.CachingParanamer;
import com.thoughtworks.paranamer.ParameterNamesNotFoundException;
import com.thoughtworks.paranamer.Paranamer;

/**
 * Paranamer implementation for {@link ParameterNameProvider}, that reads parameter info using Named annotation on each
 * parameter, or read bytecode to find parameter information, in this order.
 *
 * @author Guilherme Silveira
 */
@ApplicationScoped
@Priority(Interceptor.Priority.LIBRARY_BEFORE + 10)
public class ParanamerNameProvider implements ParameterNameProvider {
	private static final Logger logger = LoggerFactory.getLogger(ParanamerNameProvider.class);

	private final Paranamer info = new CachingParanamer(new AnnotationParanamer(new BytecodeReadingParanamer()));

	@Override
	public Parameter[] parametersFor(final AccessibleObject executable) {
		try {
			String[] names = info.lookupParameterNames(executable);
			Parameter[] params = new Parameter[names.length];
			logger.debug("Found parameter names with paranamer for {} as {}", executable, (Object) names);

			for (int i = 0; i < names.length; i++) {
				params[i] = new Parameter(i, names[i], executable);
			}

			return defensiveCopy(params);
		} catch (ParameterNamesNotFoundException e) {
			throw new IllegalStateException("Paranamer were not able to find your parameter names for " + executable
					+ "You must compile your code with debug information (javac -g), or using @Named on "
					+ "each method parameter.", e);
		}
	}

	private Parameter[] defensiveCopy(Parameter[] src) {
		Parameter[] copy = new Parameter[src.length];
		System.arraycopy(src, 0, copy, 0, src.length);
		return copy;
	}
}
