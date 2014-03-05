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

package br.com.caelum.vraptor.ioc;

/**
 * Class responsible to get instances for components.
 * 
 * @author Guilherme Silveira
 */
public interface Container {

	/**
	 * Retrieves the appropriate instance for the given class. If the container can't provide
	 * a properly instance an exception occurs.
	 * 
	 * @param type of the required component
	 * @param <T>
	 * @return the instance for registered component
	 */
	<T> T instanceFor(Class<T> type);
	
	/**
	 * Returns <code>true</code> if this container can provide an instance for
	 * the given class, <code>false</code> otherwise.
	 * 
	 * @param type of the required component
	 * @param <T>
	 * @return true iff instanceFor(type) can return a valid instance.
	 */
	<T> boolean canProvide(Class<T> type);

}
