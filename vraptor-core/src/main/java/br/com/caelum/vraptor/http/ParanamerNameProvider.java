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
import java.lang.reflect.Method;
import java.util.Arrays;
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
 * @author Otávio Scherer Garcia
 */
@ApplicationScoped
public class ParanamerNameProvider implements ParameterNameProvider {
	private static final Logger logger = LoggerFactory.getLogger(ParanamerNameProvider.class);
	
	private final Paranamer info = new AnnotationParanamer(new BytecodeReadingParanamer());
	private final CacheStore<Method, Parameter[]> cache;
	
	/** 
	 * @deprecated CDI eyes only
	 */
	public ParanamerNameProvider() {
		this(null);
	}
	
	@Inject
	public ParanamerNameProvider(CacheStore<Method, Parameter[]> cache) {
		this.cache = cache;
	}
	
	@Override
	public Parameter[] parametersFor(final AccessibleObject accessibleObject) {
		logger.debug("looking for parameters on method {}", accessibleObject);
		
		Parameter[] parameters = cache.fetch((Method) accessibleObject, new Callable<Parameter[]>() {
			
			@Override
			public Parameter[] call()
				throws Exception {
					logger.debug("putting parameters into cache for {}", accessibleObject);
					try {
						String[] names = info.lookupParameterNames(accessibleObject);
						Class<?>[] types = ((Method) accessibleObject).getParameterTypes();
						Parameter[] out = new Parameter[names.length];
						
						for (int i = 0; i < names.length; i++) {
							out[i] = new Parameter(names[i], i, types[i]);
						}
			
						if (logger.isDebugEnabled()) {
							logger.debug("found parameters {} for method {}", Arrays.toString(out), accessibleObject);
						}
						
						return out;
					} catch (ParameterNamesNotFoundException e) {
						throw new IllegalStateException("Paranamer were not able to find your parameter names for " + accessibleObject
								+ "You must compile your code with debug information (javac -g), or using @Named on "
								+ "each method parameter.", e);
					}
				}
			});
		
		return createDefensiveCopy(parameters);
	}

	private Parameter[] createDefensiveCopy(Parameter[] parameterNames) {
		Parameter[] defensiveCopy = new Parameter[parameterNames.length];
		System.arraycopy(parameterNames, 0, defensiveCopy, 0, parameterNames.length);
		return defensiveCopy;
	}
}
