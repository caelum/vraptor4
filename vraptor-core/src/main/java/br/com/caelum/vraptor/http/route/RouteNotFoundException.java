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

package br.com.caelum.vraptor.http.route;

import javax.enterprise.inject.Vetoed;

import br.com.caelum.vraptor.VRaptorException;

/**
 * A route was not found for the specified parameters.
 * 
 * @author guilherme silveira
 */
@Vetoed
public class RouteNotFoundException extends VRaptorException {

	public RouteNotFoundException(String msg) {
		super(msg);
	}

	private static final long serialVersionUID = 606801838930057251L;

}
