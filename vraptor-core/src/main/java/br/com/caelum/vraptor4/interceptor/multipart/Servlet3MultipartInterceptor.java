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
package br.com.caelum.vraptor4.interceptor.multipart;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.regex.Pattern;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.servlet.Filter;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor4.InterceptionException;
import br.com.caelum.vraptor4.Intercepts;
import br.com.caelum.vraptor4.Validator;
import br.com.caelum.vraptor4.controller.ControllerMethod;
import br.com.caelum.vraptor4.core.InterceptorStack;
import br.com.caelum.vraptor4.http.MutableRequest;
import br.com.caelum.vraptor4.interceptor.ParametersInstantiatorInterceptor;
import br.com.caelum.vraptor4.validator.I18nMessage;

import com.google.common.base.Strings;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;
import com.google.common.io.ByteStreams;
import com.google.common.io.Closeables;

/**
 * <p>
 * A multipart interceptor based on Servlet3 File Upload. It's only enabled if Servlet Part is avaliable. If you're
 * using servlet3 upload features, the {@link MultipartConfig#getDirectory()} has no effect.
 * </p>
 * <p>
 * TODO This interceptor fails in Apache Tomcat 7.x because request.getParts() doesn't work in a {@link Filter}. So you
 * need to use {@link CommonsUploadMultipartInterceptor}. See bug
 * https://issues.apache.org/bugzilla/show_bug.cgi?id=49711 and
 * https://servlet-spec-public.dev.java.net/issues/show_bug.cgi?id=14 for more info.
 * </p>
 * <p>
 * TODO According JSR315, all form fields are also avaliable via request.getParameter and request.getParameters(), but
 * not all containers implements this issue (Glassfish 3.0 fails).
 * </p>
 * 
 * @author Otávio Scherer Garcia
 * @since 3.2
 * @see DefaultMultipartConfig
 * @see NullMultipartInterceptor
 * @see MultipartInterceptor
 */

@Intercepts(before = ParametersInstantiatorInterceptor.class)
@RequestScoped
public class Servlet3MultipartInterceptor
    implements MultipartInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(Servlet3MultipartInterceptor.class);

    private static final Pattern EXTRACT_FILENAME = Pattern.compile("(.*)name=\"(.*)\"");

    public static final String ACCEPT_MULTIPART = "multipart/form-data";
    public static final String CONTENT_DISPOSITION_KEY = "content-disposition";

    private HttpServletRequest request;
    private MutableRequest parameters;
    private Validator validator;

    final Multiset<String> indexes = HashMultiset.create();

    //CDI eyes only
	@Deprecated
	public Servlet3MultipartInterceptor() {
	}
    
    @Inject
    public Servlet3MultipartInterceptor(HttpServletRequest request, MutableRequest parameters, Validator validator) {
        this.request = request;
        this.parameters = parameters;
        this.validator = validator;
    }
    
    /**
     * Only accept requests that contains multipart headers.
     */
    @Override
	public boolean accepts(ControllerMethod method) {
        if (!request.getMethod().toUpperCase().equals("POST")) {
            return false;
        }

        String contentType = request.getContentType();
        return contentType != null && contentType.startsWith(ACCEPT_MULTIPART);
    }

    @Override
	public void intercept(InterceptorStack stack, ControllerMethod method, Object controllerInstance)
        throws InterceptionException {
        logger.info("Request contains multipart data. Try to parse with Servlet3 Part");

        final Multimap<String, String> params = LinkedListMultimap.create();

        try {
            for (Part part : request.getParts()) {
                String name = part.getName();
                name = fixIndexedParameters(name);

                if (isField(part)) {
                    logger.debug("{} is a field", name);
                    params.put(name, getStringValue(part));

                } else {
                    logger.debug("{} is a file", name);

                    String fileName = getFileName(part);
                    UploadedFile upload = new DefaultUploadedFile(part.getInputStream(), fileName, part.getContentType(), part.getSize());

                    parameters.setParameter(name, name);
                    request.setAttribute(name, upload);
                }
            }
        } catch (IllegalStateException e) {
            reportSizeLimitExceeded(e);

        } catch (IOException e) {
            throw new InterceptionException(e);

        } catch (ServletException e) {
            throw new InterceptionException(e);
        }

        for (String paramName : params.keySet()) {
            Collection<String> paramValues = params.get(paramName);
            parameters.setParameter(paramName, paramValues.toArray(new String[paramValues.size()]));
        }

        stack.next(method, controllerInstance);
    }

    /**
     * This method is called when the max upload size is reached. There are no way to get the maxFileSize() and
     * maxRequestSize() attributes in a Filter.
     * 
     * @param e
     */
    protected void reportSizeLimitExceeded(final IllegalStateException e) {
        validator.add(new I18nMessage("upload", "servlet3.upload.filesize.exceeded"));
        logger.warn("The file size limit was exceeded.", e);
    }

    /**
     * Returns true if the part is a field, false otherwise.
     */
    protected boolean isField(Part part) {
        return Strings.isNullOrEmpty(part.getContentType());
    }

    /**
     * Get the filename of the part. The filename is extracted by header.
     */
    protected String getFileName(Part part) {
        String name = part.getHeader(CONTENT_DISPOSITION_KEY);
        return EXTRACT_FILENAME.matcher(name).replaceAll("$2");
    }

    /**
     * Get the content of a part as String.
     */
    protected String getStringValue(Part part)
        throws IOException {
        String encoding = request.getCharacterEncoding();

        InputStream in = part.getInputStream();
        byte[] out = ByteStreams.toByteArray(in);
        Closeables.closeQuietly(in);

        if (!Strings.isNullOrEmpty(encoding)) {
            try {
                return new String(out, encoding);
            } catch (UnsupportedEncodingException e) {
                logger.warn("Request have an invalid encoding. Ignoring it");
            }
        }

        return new String(out);
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
