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
package br.com.caelum.vraptor.ioc.fixture;

import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Produces;

@ApplicationScoped
public class ComponentFactoryInTheClasspath {
	private int callsToPreDestroy = 0 ;

	@PreDestroy
	public void preDestroy() {
		callsToPreDestroy++;
	}

	@Alternative
	public static class Provided {
		public Provided() {}
	}

	public static Provided PROVIDED = new Provided();

	@Produces @ApplicationScoped
	public Provided getInstance() {
		return PROVIDED;
	}

	public int getCallsToPreDestroy() {
		return callsToPreDestroy;
	}

}
