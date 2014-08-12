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
package br.com.caelum.vraptor.core;

import static br.com.caelum.vraptor.view.Results.logic;
import static br.com.caelum.vraptor.view.Results.page;
import static br.com.caelum.vraptor.view.Results.status;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.proxy.CDIProxies;
import br.com.caelum.vraptor.view.Results;

/**
 * An abstract result that implements all shortcut methods in the
 * recommended way
 *
 * @author Lucas Cavalcanti
 * @since 3.1.2
 */
public abstract class AbstractResult implements Result {

	@Override
	public void forwardTo(String uri) {
		use(page()).forwardTo(uri);
	}

	@Override
	public void redirectTo(String uri) {
		use(page()).redirectTo(uri);
	}

	@Override
	public <T> T forwardTo(Class<T> controller) {
		return use(logic()).forwardTo(controller);
	}

	@Override
	public <T> T redirectTo(Class<T> controller) {
		return use(logic()).redirectTo(controller);
	}

	@Override
	public <T> T of(Class<T> controller) {
		return use(page()).of(controller);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T redirectTo(T controller) {
		return (T) redirectTo(CDIProxies.extractRawTypeIfPossible(controller.getClass()));
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T forwardTo(T controller) {
		return (T) forwardTo(CDIProxies.extractRawTypeIfPossible(controller.getClass()));
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T of(T controller) {
		return (T) of(CDIProxies.extractRawTypeIfPossible(controller.getClass()));
	}

	@Override
	public void nothing() {
		use(Results.nothing());
	}

	@Override
	public void notFound() {
		use(status()).notFound();
	}

	@Override
	public void permanentlyRedirectTo(String uri) {
		use(status()).movedPermanentlyTo(uri);
	}

	@Override
	public <T> T permanentlyRedirectTo(Class<T> controller) {
		return use(status()).movedPermanentlyTo(controller);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T permanentlyRedirectTo(T controller) {
		return (T) use(status()).movedPermanentlyTo(controller.getClass());
	}

}