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

import static br.com.caelum.vraptor.view.Results.representation;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.EnumSet;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.config.Configuration;
import br.com.caelum.vraptor.controller.HttpMethod;
import br.com.caelum.vraptor.http.route.Router;
import br.com.caelum.vraptor.proxy.MethodInvocation;
import br.com.caelum.vraptor.proxy.Proxifier;
import br.com.caelum.vraptor.proxy.SuperMethod;

import com.google.common.base.Joiner;

/**
 * Allows header related results.
 *
 * @author guilherme silveira
 * @since 3.0.3
 */
@RequestScoped
public class DefaultStatus implements Status {

	private final HttpServletResponse response;
	private final Result result;
	private final Configuration config;
	private final Router router;
	private final Proxifier proxifier;

	/** 
	 * @deprecated CDI eyes only
	 */
	protected DefaultStatus() {
		this(null, null, null, null, null);
	}

	@Inject
	public DefaultStatus(HttpServletResponse response, Result result, Configuration config,
			Proxifier proxifier, Router router) {
		this.response = response;
		this.result = result;
		this.config = config;
		this.proxifier = proxifier;
		this.router = router;
	}

	@Override
	public void notFound() {
		sendError(HttpServletResponse.SC_NOT_FOUND);
	}

	private void sendError(int error) {
		try {
			response.sendError(error);
		} catch (IOException e) {
			throw new ResultException(e);
		}
	}

	private void sendError(int error, String message) {
		try {
			response.sendError(error, message);
		} catch (IOException e) {
			throw new ResultException(e);
		}
	}

	@Override
	public void header(String key, String value) {
		response.addHeader(key, value);
	}

	@Override
	public void created() {
		response.setStatus(HttpServletResponse.SC_CREATED);
		result.use(Results.nothing());
	}

	@Override
	public void created(String location) {
		header("Location", fixLocation(location));
		created();
	}

	@Override
	public void ok() {
		response.setStatus(HttpServletResponse.SC_OK);
		result.use(Results.nothing());
	}

	@Override
	public void conflict() {
		sendError(HttpServletResponse.SC_CONFLICT);
	}

	@Override
	public void methodNotAllowed(EnumSet<HttpMethod> allowedMethods) {
		header("Allow", Joiner.on(", ").join(allowedMethods));
		sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
	}

	@Override
	public void movedPermanentlyTo(String location) {
		this.response.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
		header("Location", fixLocation(location));

		this.response.addIntHeader("Content-length", 0);
		this.response.addDateHeader("Date", System.currentTimeMillis());
	}

	private String fixLocation(String location) {
		if (location.startsWith("/")) {
			return config.getApplicationPath() + location;
		}
		return location;
	}

	@Override
	public <T> T movedPermanentlyTo(final Class<T> controller) {
		return proxifier.proxify(controller, new MethodInvocation<T>() {
			@Override
			public Object intercept(T proxy, Method method, Object[] args, SuperMethod superMethod) {
				String uri = router.urlFor(controller, method, args);
				movedPermanentlyTo(uri);
				return null;
			}
		});
	}

	@Override
	public void unsupportedMediaType(String message) {
		sendError(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE, message);
	}

	@Override
	public void badRequest(String message) {
		sendError(HttpServletResponse.SC_BAD_REQUEST, message);
	}

	@Override
	public void badRequest(List<?> errors) {
		response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		result.use(representation()).from(errors, "errors").serialize();
	}

	@Override
	public void forbidden(String message) {
		sendError(HttpServletResponse.SC_FORBIDDEN, message);
	}

	@Override
	public void noContent() {
		response.setStatus(HttpServletResponse.SC_NO_CONTENT);
	}

	@Override
	public void notAcceptable() {
		sendError(HttpServletResponse.SC_NOT_ACCEPTABLE);
	}

	@Override
	public void accepted(){
		response.setStatus(HttpServletResponse.SC_ACCEPTED );
		result.use(Results.nothing());
	}

	@Override
	public void notModified() {
		response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
	}
	
	@Override
	public void notImplemented() {
		response.setStatus(HttpServletResponse.SC_NOT_IMPLEMENTED);
	}

	@Override
	public void internalServerError() {
		response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
	}
	
}
