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
package br.com.caelum.vraptor.interceptor.example;

import javax.enterprise.inject.Vetoed;

import br.com.caelum.vraptor.AfterCall;
import br.com.caelum.vraptor.AroundCall;
import br.com.caelum.vraptor.BeforeCall;
import br.com.caelum.vraptor.Intercepts;
import br.com.caelum.vraptor.interceptor.AcceptsWithAnnotations;
import br.com.caelum.vraptor.interceptor.CustomAcceptsFailCallback;
import br.com.caelum.vraptor.interceptor.SimpleInterceptorStack;

@Intercepts
@AcceptsWithAnnotations(NotLogged.class)
@Vetoed
public class InterceptorWithCustomizedAccepts {
	
	private boolean interceptCalled;
	private boolean beforeCalled;
	private boolean afterCalled;

	@AroundCall
	public void intercept(SimpleInterceptorStack stack) {
		this.interceptCalled = true;
	}		

	@BeforeCall
	public void before() {
		this.beforeCalled = true;
	}

	@AfterCall
	public void after() {
		this.afterCalled = true;
	}

	public boolean isInterceptCalled() {
		return interceptCalled;
	}

	public boolean isBeforeCalled() {
		return beforeCalled;
	}

	public boolean isAfterCalled() {
		return afterCalled;
	}

	@CustomAcceptsFailCallback
	public void customAcceptsFailCallback() {
	}
	
	
}
