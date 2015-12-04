/***
 * Copyright (c) 2009 Caelum - www.caelum.com.br/opensource
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package br.com.caelum.vraptor.view;

import static br.com.caelum.vraptor.view.Results.logic;
import static br.com.caelum.vraptor.view.Results.page;
import static com.google.common.base.Preconditions.checkState;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.controller.ControllerMethod;
import br.com.caelum.vraptor.controller.HttpMethod;
import br.com.caelum.vraptor.core.ReflectionProvider;
import br.com.caelum.vraptor.http.MutableRequest;
import br.com.caelum.vraptor.http.ParametersProvider;
import br.com.caelum.vraptor.http.route.ControllerNotFoundException;
import br.com.caelum.vraptor.http.route.MethodNotAllowedException;
import br.com.caelum.vraptor.http.route.Router;
import br.com.caelum.vraptor.validator.Message;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RequestScoped
public class DefaultRefererResult implements RefererResult {
	
	private static final Logger logger = LoggerFactory.getLogger(DefaultRefererResult.class);
	
	private final MutableRequest request;
	private final Result result;
	private final Router router;
	private final ParametersProvider provider;
	private ReflectionProvider reflectionProvider;

	/** 
	 * @deprecated CDI eyes only
	 */
	protected DefaultRefererResult() {
		this(null, null, null, null, null);
	}

	@Inject
	public DefaultRefererResult(Result result, MutableRequest request, Router router, ParametersProvider provider,
			ReflectionProvider reflectionProvider) {
		this.result = result;
		this.request = request;
		this.router = router;
		this.provider = provider;
		this.reflectionProvider = reflectionProvider;
	}

	@Override
	public void forward() throws IllegalStateException {
		String referer = getReferer();

		try {
			ControllerMethod method = router.parse(referer, HttpMethod.GET, request);
			executeMethod(method, result.use(logic()).forwardTo(method.getController().getType()));
		} catch (ControllerNotFoundException | MethodNotAllowedException e) {
			logger.warn("Could not find or doesn't allowed to get controller method", e);
			result.use(page()).forwardTo(referer);
		}
	}

	private void executeMethod(ControllerMethod method, Object instance) {
		Object[] args = provider.getParametersFor(method, new ArrayList<Message>());
		reflectionProvider.invoke(instance, method.getMethod(), args);
	}

	@Override
	public void redirect() throws IllegalStateException {
		String referer = getReferer();
		try {
			ControllerMethod method = router.parse(referer, HttpMethod.GET, request);
			executeMethod(method, result.use(logic()).redirectTo(method.getController().getType()));
		} catch (ControllerNotFoundException | MethodNotAllowedException e) {
			logger.warn("Could not find or doesn't allowed to get controller method", e);
			result.use(page()).redirectTo(referer);
		}
	}

	protected String getReferer() {
		String referer = request.getHeader("Referer");
		checkState(referer != null, "The Referer header was not specified");

		String refererPath = null;
		try {
			refererPath = new URL(referer).getPath();
		} catch(MalformedURLException e) {
			//Maybe a relative path?
			refererPath = referer;
		}
		String ctxPath = request.getContextPath();
		
		//if the context path is not in the beggining we should return the entire path
		//this is useful for proxied app servers which hide the ctx path from url
		return refererPath.startsWith(ctxPath+"/") || refererPath.equals(ctxPath) ? refererPath.substring(ctxPath.length()) : refererPath;
	}

}
