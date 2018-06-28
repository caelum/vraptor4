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

import static com.google.common.base.Strings.isNullOrEmpty;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.Annotated;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;


/**
 * A producer that allows us to inject environment properties. i.e
 * 
 * 	<pre>
 * 		<code>
 * 			\@Inject \@Qualifier("mail.server.host") 
 * 			private String mailHost;
 * 		</code>
 * 	</pre>
 * 
 * @author Rodrigo Turini
 * @since 4.0
 */
@ApplicationScoped
public class EnvironmentPropertyProducer {

	private final Environment environment;

	/**
	 * @deprecated CDI eyes only
	 */
	protected EnvironmentPropertyProducer() {
		this(null);
	}
	
	@Inject
	public EnvironmentPropertyProducer(Environment environment) {
		this.environment = environment;
	}
	
	@Produces @Property
	public String get(InjectionPoint ip) {
		Annotated annotated = ip.getAnnotated();
		Property property = annotated.getAnnotation(Property.class);
		String key = property.value();
		if (isNullOrEmpty(key)) {
			key = ip.getMember().getName();
		}
		
		String defaultValue = property.defaultValue();
		if(!isNullOrEmpty(defaultValue)){
			return environment.get(key, defaultValue);
		}
		
		return environment.get(key);
	}
}
