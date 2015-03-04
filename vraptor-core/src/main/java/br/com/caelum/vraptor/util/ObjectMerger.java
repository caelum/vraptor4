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
package br.com.caelum.vraptor.util;

import java.lang.reflect.InvocationTargetException;

import javax.enterprise.context.RequestScoped;

import org.apache.commons.beanutils.BeanUtilsBean;

/**
 * This classes is used to merge 2 objects ignoring Null values.
 * 
 * It's used by our parameter's resolvers
 *
 * @author Nykolas Lima
 */
@RequestScoped
public class ObjectMerger {

	public Object merge(Object dest, Object from) {
		try {
			new BeanUtilsBean() {
			    @Override
			    public void copyProperty(Object dest, String name, Object value) throws IllegalAccessException, InvocationTargetException {
			        //do not override null values
			    	if(value != null) {
			            super.copyProperty(dest, name, value);
			        }
			    }
			}.copyProperties(dest, from);
			
			return dest;
		} catch (IllegalAccessException | InvocationTargetException e) {
			throw new ObjectMergeException(String.format("Error trying to merge values from %s", dest.getClass().getName()), e);
		}
	}
	
}
