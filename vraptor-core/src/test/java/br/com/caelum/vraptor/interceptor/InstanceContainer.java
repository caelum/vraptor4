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
package br.com.caelum.vraptor.interceptor;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import br.com.caelum.vraptor.VRaptorException;
import br.com.caelum.vraptor.ioc.Container;

/**
 * A simple container implementation used for tests
 * 
 * @author guilherme silveira
 */
public class InstanceContainer implements Container {
	
	public final List<Object> instances;
	
	public InstanceContainer(Object  ...objects) {
		instances = new LinkedList<>(Arrays.asList(objects));
	}

	@Override
	public <T> boolean canProvide(Class<T> type) {
		for(Object o : instances) {
			if(type.isAssignableFrom(o.getClass())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public <T> T instanceFor(Class<T> type) {
		T choosen = null;
		for(Object o : instances) {
			if(type.isAssignableFrom(o.getClass())) {
				choosen = type.cast(o);
			}
		}
		if(choosen!=null){
			return choosen;
		}	
		throw new VRaptorException("Type "+type+" was not found");
	}

	public boolean isEmpty() {
		return instances.isEmpty();
	}

}
