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

import static java.util.Collections.unmodifiableList;

import java.lang.reflect.AccessibleObject;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor.cache.CacheStore;

import com.thoughtworks.paranamer.AnnotationParanamer;
import com.thoughtworks.paranamer.BytecodeReadingParanamer;
import com.thoughtworks.paranamer.ParameterNamesNotFoundException;
import com.thoughtworks.paranamer.Paranamer;

/**
 * Paranamer implementation for {@link ParameterNameProvider}, that reads parameter info using Named annotation on each
 * parameter, or read bytecode to find parameter information, in this order.
 *
 * @author Guilherme Silveira
 */
@ApplicationScoped
public class ParanamerNameProvider implements ParameterNameProvider {
	private static final Logger logger = LoggerFactory.getLogger(ParanamerNameProvider.class);

	private final Paranamer info = new AnnotationParanamer(new BytecodeReadingParanamer());
	private final CacheStore<AccessibleObject, List<Parameter>> cache;

	protected ParanamerNameProvider() {
		this(null);
	}

	@Inject
	public ParanamerNameProvider(CacheStore<AccessibleObject, List<Parameter>> cache) {
		this.cache = cache;
	}

	@Override
	public List<Parameter> parametersFor(final AccessibleObject executable) {
		return cache.fetch(executable, new Callable<List<Parameter>>() {
			@Override
			public List<Parameter> call() throws Exception {
				try {
					List<Parameter> params = new ArrayList<>();
					String[] names = info.lookupParameterNames(executable);
					logger.debug("Found parameter names with paranamer for {} as {}", executable, (Object) names);
					
					for (int i = 0; i < names.length; i++) {
						params.add(new Parameter(i, names[i], executable));
					}

					return unmodifiableList(params);
				} catch (ParameterNamesNotFoundException e) {
					throw new IllegalStateException("Paranamer were not able to find your parameter names for " + executable
							+ "You must compile your code with debug information (javac -g), or using @Named on "
							+ "each method parameter.", e);
				}
			}
		});
	}
}
