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
package br.com.caelum.vraptor.environment;

import static com.google.common.base.Objects.firstNonNull;

import java.io.IOException;
import java.security.AccessController;
import java.security.PrivilegedAction;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.ServletContext;

/**
 * A default {@link Environment} implementation which loads the environment file
 * based on <i>VRAPTOR_ENV</i> system property or <i>br.com.caelum.vraptor
 * .environment</i> property in the context init parameter.
 * 
 * @author Alexandre Atoji
 * @author Andrew Kurauchi
 * @author Guilherme Silveira
 * @author Rodrigo Turini
 */
@ApplicationScoped
@Named("environment")
public class ServletBasedEnvironment extends DefaultEnvironment {

	public static final String ENVIRONMENT_PROPERTY = "br.com.caelum.vraptor.environment";

	/**
	 * @deprecated CDI eyes only
	 */
	public ServletBasedEnvironment() throws IOException {
	}

	@Inject
	public ServletBasedEnvironment(ServletContext context) throws IOException {
		super(new EnvironmentType(env(context)));
	}

	private static String env(ServletContext context) {
		String systemEnv = AccessController.doPrivileged(new PrivilegedAction<String>() {
			@Override
			public String run() {
				return System.getenv("VRAPTOR_ENV");
			}
		});

		if (systemEnv != null) {
			return systemEnv;
		}

		String systemProperty = AccessController.doPrivileged(new PrivilegedAction<String>() {
			@Override
			public String run() {
				return System.getProperty(ENVIRONMENT_PROPERTY);
			}
		});

		if (systemProperty != null) {
			return systemProperty;
		}

		String containerProperty = context.getInitParameter(ENVIRONMENT_PROPERTY);
		
		return firstNonNull(containerProperty, "development");
	}
}