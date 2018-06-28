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
package br.com.caelum.vraptor.core;

import static com.google.common.base.MoreObjects.firstNonNull;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.jstl.core.Config;
import javax.servlet.jsp.jstl.fmt.LocalizationContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor.util.EmptyBundle;
import br.com.caelum.vraptor.util.SafeResourceBundle;

/**
 * The default implementation of bundle provider uses JSTL's api to access user information on the bundle to be used.
 *
 * @author Guilherme Silveira
 * @author Otávio Scherer Garcia
 */
@RequestScoped
public class JstlLocalization {

	private static final Logger logger = LoggerFactory.getLogger(JstlLocalization.class);

	private static final String DEFAULT_BUNDLE_NAME = "messages";

	private final HttpServletRequest request;

	/**
	 * @deprecated CDI eyes only
	 */
	protected JstlLocalization() {
		this(null);
	}

	@Inject
	public JstlLocalization(HttpServletRequest request) {
		this.request = request;
	}

	@Produces
	public ResourceBundle getBundle(Locale locale) {
		Object bundle = findByKey(Config.FMT_LOCALIZATION_CONTEXT);
		ResourceBundle unsafe = extractUnsafeBundle(bundle, locale);
		return new SafeResourceBundle(unsafe);
	}

	private ResourceBundle extractUnsafeBundle(Object bundle, Locale locale) {
		if (bundle instanceof String || bundle == null) {
			String baseName = firstNonNull((String) bundle, DEFAULT_BUNDLE_NAME);

			try {
				return ResourceBundle.getBundle(baseName, locale);
			} catch (MissingResourceException e) {
				logger.warn("Couldn't find message bundle for base name '{}' and locale '{}', creating an empty one", baseName, locale);
				return new EmptyBundle();
			}
		}

		if (bundle instanceof LocalizationContext) {
			return ((LocalizationContext) bundle).getResourceBundle();
		}

		logger.warn("Can't handle bundle '{}'. Please report this bug. Using an empty bundle", bundle);
		return new EmptyBundle();
	}

	@Produces
	public Locale getLocale() {
		Locale localeFromConfig = localeFor(Config.FMT_LOCALE);
		return firstNonNull(localeFromConfig, Locale.getDefault());
	}

	private Locale localeFor(String key) {
		Object localeValue = findByKey(key);

		if (localeValue instanceof String) {
			String languageTag = localeValue.toString().replace("_", "-");
			return Locale.forLanguageTag(languageTag);
		} else if (localeValue instanceof Locale) {
			return (Locale) localeValue;
		}

		return request.getLocale();
	}

	/**
	 * Looks up a configuration variable in the request, session and application scopes. If none is found, return by
	 * {@link ServletContext#getInitParameter(String)} method.
	 */
	private Object findByKey(String key) {
		Object value = Config.get(request, key);
		if (value != null) {
			return value;
		}

		value = Config.get(request.getSession(createNewSession()), key);
		if (value != null) {
			return value;
		}

		value = Config.get(request.getServletContext(), key);
		if (value != null) {
			return value;
		}

		return request.getServletContext().getInitParameter(key);
	}

	protected boolean createNewSession(){
	    return false;
	}
}
