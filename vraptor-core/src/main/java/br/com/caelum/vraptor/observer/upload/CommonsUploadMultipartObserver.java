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

import static com.google.common.base.Strings.isNullOrEmpty;
import static org.apache.commons.fileupload.disk.DiskFileItemFactory.DEFAULT_SIZE_THRESHOLD;
import static org.slf4j.LoggerFactory.getLogger;

import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.servlet.ServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadBase.SizeLimitExceededException;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.slf4j.Logger;

import br.com.caelum.vraptor.events.ControllerFound;
import br.com.caelum.vraptor.http.MutableRequest;
import br.com.caelum.vraptor.validator.I18nMessage;
import br.com.caelum.vraptor.validator.Validator;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;

/**
 * A multipart observer based on Apache Commons FileUpload.
 *
 * @author Guilherme Silveira
 * @author Otávio Scherer Garcia
 * @author Rodrigo Turini
 */
@ApplicationScoped
public class CommonsUploadMultipartObserver {

	private static final Logger logger = getLogger(CommonsUploadMultipartObserver.class);

	public void upload(@Observes ControllerFound event, MutableRequest request,
			MultipartConfig config, Validator validator) {

		if (!ServletFileUpload.isMultipartContent(request)) {
			return;
		}

		logger.info("Request contains multipart data. Try to parse with commons-upload.");

		final Multiset<String> indexes = HashMultiset.create();
		final Multimap<String, String> params = LinkedListMultimap.create();

		ServletFileUpload uploader = createServletFileUpload(config);

		UploadSizeLimit uploadSizeLimit = event.getMethod().getMethod().getAnnotation(UploadSizeLimit.class);
		uploader.setSizeMax(uploadSizeLimit != null ? uploadSizeLimit.sizeLimit() : config.getSizeLimit());
		uploader.setFileSizeMax(uploadSizeLimit != null ? uploadSizeLimit.fileSizeLimit() : config.getFileSizeLimit());
		logger.debug("Setting file sizes: total={}, file={}", uploader.getSizeMax(), uploader.getFileSizeMax());

		try {
			final List<FileItem> items = uploader.parseRequest(request);
			logger.debug("Found {} attributes in the multipart form submission. Parsing them.", items.size());


			for (FileItem item : items) {
				String name = item.getFieldName();
				name = fixIndexedParameters(name, indexes);

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
			reportFileUploadException(e, validator);
		}
	}

	private boolean isNotEmpty(FileItem item) {
		return !item.getName().isEmpty();
	}

	/**
	 * This method is called when the {@link SizeLimitExceededException} was thrown.
	 */
	protected void reportSizeLimitExceeded(final SizeLimitExceededException e, Validator validator) {
		validator.add(new I18nMessage("upload", "file.limit.exceeded", e.getActualSize(), e.getPermittedSize()));
		logger.warn("The file size limit was exceeded. Actual {} permitted {}", e.getActualSize(), e.getPermittedSize());
	}

	protected void reportFileUploadException(FileUploadException e, Validator validator) {
		validator.add(new I18nMessage("upload", "file.upload.exception"));
		logger.warn("There was some problem parsing this multipart request, "
				+ "or someone is not sending a RFC1867 compatible multipart request.", e);
	}

	protected void processFile(FileItem item, String name, MutableRequest request) {
		UploadedFile upload = new CommonsUploadedFile(item);
		request.setParameter(name, name);
		request.setAttribute(name, upload);

		logger.debug("Uploaded file: {} with {}", name, upload);
	}

	protected ServletFileUpload createServletFileUpload(MultipartConfig config) {
		FileItemFactory factory = new DiskFileItemFactory(DEFAULT_SIZE_THRESHOLD, config.getDirectory());
		logger.debug("Using repository {} for file upload", config.getDirectory());

		return new ServletFileUpload(factory);
	}

	protected String getValue(FileItem item, ServletRequest request) {
		String encoding = request.getCharacterEncoding();
		if (!isNullOrEmpty(encoding)) {
			try {
				return item.getString(encoding);
			} catch (UnsupportedEncodingException e) {
				logger.debug("Request has an invalid encoding. Ignoring it", e);
			}
		}
		return item.getString();
	}

	protected String fixIndexedParameters(String name, Multiset<String> indexes) {
		if (name.contains("[]")) {
			String newName = name.replace("[]", "[" + (indexes.count(name)) + "]");
			indexes.add(name);
			logger.debug("{} was renamed to {}", name, newName);

			return newName;
		}
		return name;
	}
}
