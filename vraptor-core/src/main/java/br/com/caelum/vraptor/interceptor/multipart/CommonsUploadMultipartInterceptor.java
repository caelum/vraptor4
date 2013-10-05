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
package br.com.caelum.vraptor.interceptor.multipart;

import static com.google.common.base.Objects.firstNonNull;
import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.IOException;
import java.util.Collection;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadBase.SizeLimitExceededException;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor.Intercepts;
import br.com.caelum.vraptor.Validator;
import br.com.caelum.vraptor.controller.ControllerMethod;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.http.InvalidParameterException;
import br.com.caelum.vraptor.http.MutableRequest;
import br.com.caelum.vraptor.interceptor.Interceptor;
import br.com.caelum.vraptor.interceptor.ParametersInstantiatorInterceptor;
import br.com.caelum.vraptor.validator.I18nMessage;

import com.google.common.base.Throwables;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;

/**
 * A multipart interceptor based on Apache Commons Upload. Provided parameters are injected through
 * {@link HttpServletRequest#setAttribute(String, Object)} and uploaded files are made available through.
 *
 * @author Guilherme Silveira
 * @author Otávio Scherer Garcia
 */
@Intercepts(before=ParametersInstantiatorInterceptor.class)
@RequestScoped
public class CommonsUploadMultipartInterceptor implements Interceptor {

	private static final Logger logger = LoggerFactory.getLogger(CommonsUploadMultipartInterceptor.class);

	private MutableRequest request;
	private Validator validator;
	private ServletFileUpload uploader;

	private Multiset<String> indexes;
	private Multimap<String, String> params;

	//CDI eyes only
	@Deprecated
	public CommonsUploadMultipartInterceptor() {
	}

	@Inject
	public CommonsUploadMultipartInterceptor(MutableRequest request, Validator validator, ServletFileUpload uploader) {
		this.request = request;
		this.validator = validator;
		this.uploader = uploader;
	}

	/**
	 * Will intercept the request if apache file upload says that this request is multipart
	 */
	@Override
	public boolean accepts(ControllerMethod method) {
		return ServletFileUpload.isMultipartContent(request);
	}
	
	@Override
	public void intercept(InterceptorStack stack, ControllerMethod method, Object controllerInstance) {
		logger.info("Request contains multipart data. Try to parse with commons-upload.");

		params = LinkedListMultimap.create();
		indexes = HashMultiset.create();

		try {
			FileItemIterator items = uploader.getItemIterator(request);

			while(items.hasNext()) {
				FileItemStream item = items.next();
				
				String name = fixIndexedParameters(item.getFieldName());
				logger.debug("processing {} as {}", name, (item.isFormField() ? "field" : "file"));

				if (item.isFormField()) {
					processField(item, name);
				} else {
					processFile(item, name);
				}
			}

			for (String paramName : params.keySet()) {
				Collection<String> paramValues = params.get(paramName);
				request.setParameter(paramName, paramValues.toArray(new String[paramValues.size()]));
			}

		} catch (final SizeLimitExceededException e) {
			reportSizeLimitExceeded(e);

		} catch (FileUploadException | IOException e) {
			logger.warn("There was some problem parsing this multipart request, "
					+ "or someone is not sending a RFC1867 compatible multipart request.", e);
			throw Throwables.propagate(e);
		}

		stack.next(method, controllerInstance);
	}

	/**
	 * This method is called when the {@link SizeLimitExceededException} was thrown.
	 *
	 * @param e
	 */
	protected void reportSizeLimitExceeded(final SizeLimitExceededException e) {
		validator.add(new I18nMessage("upload", "file.limit.exceeded", e.getActualSize(), e.getPermittedSize()));
		logger.warn("The file size limit was exceeded.", e);
	}

	protected void processFile(FileItemStream item, String name) {
		try {
			UploadedFile upload = new DefaultUploadedFile(item.openStream(), item.getName(), item.getContentType());
			request.setParameter(name, name);
			request.setAttribute(name, upload);

			logger.debug("Uploaded file: {} with {}", name, upload);
		} catch (IOException e) {
			throw new InvalidParameterException("Cant parse uploaded file " + item.getName(), e);
		}
	}

	protected void processField(FileItemStream item, String name) {
		try {
			String encoding = firstNonNull(request.getCharacterEncoding(), UTF_8.name());
			String value = Streams.asString(item.openStream(), encoding);
			params.put(name, value);
		} catch (IOException e) {
			throw new InvalidParameterException("Cant parse field " + item.getName(), e);
		}
	}

	protected String fixIndexedParameters(String name) {
		if (name.contains("[]")) {
			String newName = name.replace("[]", "[" + (indexes.count(name)) + "]");
			indexes.add(name);
			logger.debug("{} was renamed to {}", name, newName);
			name = newName;
		}
		return name;
	}
}