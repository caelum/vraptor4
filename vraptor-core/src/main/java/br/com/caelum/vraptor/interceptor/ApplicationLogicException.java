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

import javax.enterprise.inject.Vetoed;

import br.com.caelum.vraptor.InterceptionException;


/**
 * When a controller or JSP throws an exception, we use this one to wrap it, so
 * we can unwrap after it leaves the interceptor stack
 *
 */
@Vetoed
public class ApplicationLogicException extends InterceptionException {

	private static final long serialVersionUID = -8388907262726903974L;

	public ApplicationLogicException(String msg) {
		super(msg);
	}

	public ApplicationLogicException(String msg, Throwable e) {
		super(msg, e);
	}

	public ApplicationLogicException(Throwable e) {
		super(e);
	}
}
