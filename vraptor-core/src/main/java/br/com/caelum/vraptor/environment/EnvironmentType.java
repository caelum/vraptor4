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

import java.util.Objects;

import javax.enterprise.inject.Vetoed;

/**
 * An class to represent usual {@link Environment} types 
 * 
 * @author Rodrigo Turini
 * @author Chico Sokol
 * @since 4.0
 */
@Vetoed
public class EnvironmentType {
	
	public static final EnvironmentType PRODUCTION = new EnvironmentType("production");
	public static final EnvironmentType DEVELOPMENT = new EnvironmentType("development");
	public static final EnvironmentType TEST = new EnvironmentType("test");

	private final String name;
	
	public EnvironmentType(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}
	
	@Override
	public int hashCode() {
		return Objects.hashCode(name);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		EnvironmentType other = (EnvironmentType) obj;
		return Objects.equals(name, other.name);
	}
}
