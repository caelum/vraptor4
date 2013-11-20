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
package br.com.caelum.vraptor.observer.upload;

import static org.slf4j.LoggerFactory.getLogger;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadBase.SizeLimitExceededException;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;

import br.com.caelum.vraptor.events.ControllerMethodDiscovered;
import br.com.caelum.vraptor.http.InvalidParameterException;
import br.com.caelum.vraptor.http.MutableRequest;
import br.com.caelum.vraptor.validator.I18nMessage;
import br.com.caelum.vraptor.validator.Validator;

import com.google.common.base.Strings;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;

/**
 * A multipart observer based on Apache Commons Upload. Provided parameters are injected through
 * {@link HttpServletRequest#setAttribute(String, Object)} and uploaded files are made available through.
 *
 * @author Guilherme Silveira
 * @author Otávio Scherer Garcia
 * @author Rodrigo Turini
 */
@ApplicationScoped
public class CommonsUploadMultipartObserver {

	private static final Logger logger = getLogger(CommonsUploadMultipartObserver.class);

	private Multiset<String> indexes;
	private Multimap<String, String> params;

	public void upload(@Observes ControllerMethodDiscovered event, MutableRequest request,
			MultipartConfig config, Validator validator) {

		if (!ServletFileUpload.isMultipartContent(request)) return;

		logger.info("Request contains multipart data. Try to parse with commons-upload.");

		indexes = HashMultiset.create();
		params = LinkedListMultimap.create();

		ServletFileUpload uploader = createServletFileUpload(config);
		uploader.setSizeMax(config.getSizeLimit());

		try {
			final List<FileItem> items = uploader.parseRequest(request);
			logger.debug("Found {} attributes in the multipart form submission. Parsing them.", items.size());


			for (FileItem item : items) {
				String name = item.getFieldName();
				name = fixIndexedParameters(name);

				if (item.isFormField()) {
					logger.debug("{} is a field", name);
					params.put(name, getValue(item, request));

				} else if (isNotEmpty(item)) {
					logger.debug("{} is a file", name);
					processFile(item, name, request);

				} else {
					logger.debug("A file field is empty: {}", item.getFieldName());
				}
			}

			for (String paramName : params.keySet()) {
				Collection<String> paramValues = params.get(paramName);
				request.setParameter(paramName, paramValues.toArray(new String[paramValues.size()]));
			}

		} catch (final SizeLimitExceededException e) {
			reportSizeLimitExceeded(e, validator);

		} catch (FileUploadException e) {
			logger.warn("There was some problem parsing this multipart request, "
					+ "or someone is not sending a RFC1867 compatible multipart request.", e);
		}
	}

	private boolean isNotEmpty(FileItem item) {
		return item.getName().length() > 0;
	}

	/**
	 * This method is called when the {@link SizeLimitExceededException} was thrown.
	 */
	protected void reportSizeLimitExceeded(final SizeLimitExceededException e, Validator validator) {
		validator.add(new I18nMessage("upload", "file.limit.exceeded", e.getActualSize(), e.getPermittedSize()));
		logger.warn("The file size limit was exceeded.", e);
	}

	protected void processFile(FileItem item, String name, MutableRequest request) {
		try {
			String fileName = FilenameUtils.getName(item.getName());
			UploadedFile upload = new DefaultUploadedFile(item.getInputStream(), fileName, item.getContentType(), item.getSize());
			request.setParameter(name, name);
			request.setAttribute(name, upload);

			logger.debug("Uploaded file: {} with {}", name, upload);
		} catch (IOException e) {
			throw new InvalidParameterException("Cant parse uploaded file " + item.getName(), e);
		}
	}

	protected ServletFileUpload createServletFileUpload(MultipartConfig config) {
		DiskFileItemFactory factory = new DiskFileItemFactory();
		factory.setRepository(config.getDirectory());

		logger.debug("Using repository {} for file upload", factory.getRepository());

		return new ServletFileUpload(factory);
	}

	protected String getValue(FileItem item, ServletRequest request) {
		String encoding = request.getCharacterEncoding();
		if (!Strings.isNullOrEmpty(encoding)) {
			try {
				return item.getString(encoding);
			} catch (UnsupportedEncodingException e) {
				logger.warn("Request have an invalid encoding. Ignoring it");
			}
		}
		return item.getString();
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