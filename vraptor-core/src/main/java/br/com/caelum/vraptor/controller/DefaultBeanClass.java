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

package br.com.caelum.vraptor.controller;

import java.lang.annotation.Annotation;
import java.util.Objects;

import javax.enterprise.inject.Vetoed;

@Vetoed
public class DefaultBeanClass implements BeanClass {

	private final Class<?> type;

	public DefaultBeanClass(Class<?> type) {
		this.type = type;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;
		
		DefaultBeanClass other = (DefaultBeanClass) obj;
		return Objects.equals(type, other.type);
	}

	@Override
	public int hashCode() {
		return Objects.hash(type);
	}

	@Override
	public String toString() {
		return "{ControllerClass " + type.getName() + "}";
	}

	@Override
	public Class<?> getType() {
		return type;
	}

	@Override
	public Annotation[] getAnnotations(){
		return type.getAnnotations();
	}

	@Override
	public Package getPackage(){
		return type.getPackage();
	}

	@Override
	public String getPackageName(){
		return getPackage().getName();
	}

}
