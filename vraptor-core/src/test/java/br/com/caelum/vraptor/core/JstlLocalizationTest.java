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

import static javax.servlet.jsp.jstl.core.Config.FMT_LOCALE;
import static javax.servlet.jsp.jstl.core.Config.FMT_LOCALIZATION_CONTEXT;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assume.assumeThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ListResourceBundle;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.jstl.core.Config;
import javax.servlet.jsp.jstl.fmt.LocalizationContext;

import org.junit.Before;
import org.junit.Test;

/**
 * Test class for {@link JstlLocalization}.
 *
 * @author Otávio Scherer Garcia
 */
public class JstlLocalizationTest {

	private static final Locale PT_BR = new Locale("pt", "BR");

	private JstlLocalization localization;

	private HttpServletRequest request;
	private ServletContext servletContext;
	private HttpSession session;

	@Before
	public void setUp() {
		request = mock(HttpServletRequest.class);
		servletContext = mock(ServletContext.class);
		session = mock(HttpSession.class);

		localization = new JstlLocalization(request);

		ResourceBundle bundle = new ListResourceBundle() {
			@Override
			protected Object[][] getContents() {
				return new Object[][] { { "my.key", "abc" } };
			}
		};

		LocalizationContext context = new LocalizationContext(bundle);
		when(request.getAttribute(FMT_LOCALIZATION_CONTEXT + ".request")).thenReturn(context);

		when(request.getSession(false)).thenReturn(session);
		when(request.getServletContext()).thenReturn(servletContext);
	}

	@Test
	public void shouldGetLocaleFromRequestFirst() {
		when(request.getAttribute(FMT_LOCALE + ".request")).thenReturn(PT_BR);
		when(session.getAttribute(FMT_LOCALE + ".session")).thenReturn(Locale.ENGLISH);
		when(servletContext.getAttribute(FMT_LOCALE + ".application")).thenReturn(Locale.ENGLISH);
		when(servletContext.getInitParameter(Config.FMT_LOCALE)).thenReturn(Locale.ENGLISH.toString());
		when(request.getLocale()).thenReturn(Locale.ENGLISH);
		assumeThat(Locale.getDefault(), is(Locale.ENGLISH));

		assertThat(localization.getLocale(), equalTo(PT_BR));
	}

	@Test
	public void shouldGetLocaleFromSessionWhenNotFoundInRequest() {
		when(session.getAttribute(FMT_LOCALE + ".session")).thenReturn(PT_BR);
		when(servletContext.getAttribute(FMT_LOCALE + ".application")).thenReturn(Locale.ENGLISH);
		when(servletContext.getInitParameter(Config.FMT_LOCALE)).thenReturn(Locale.ENGLISH.toString());
		when(request.getLocale()).thenReturn(Locale.ENGLISH);
		assumeThat(Locale.getDefault(), is(Locale.ENGLISH));

		assertThat(localization.getLocale(), equalTo(PT_BR));
	}

	@Test
	public void shouldGetLocaleFromServletContextWhenNotFoundInSession() {
		when(servletContext.getAttribute(FMT_LOCALE + ".application")).thenReturn(PT_BR);
		when(servletContext.getInitParameter(Config.FMT_LOCALE)).thenReturn(Locale.ENGLISH.toString());
		when(request.getLocale()).thenReturn(Locale.ENGLISH);
		assumeThat(Locale.getDefault(), is(Locale.ENGLISH));

		assertThat(localization.getLocale(), equalTo(PT_BR));
	}

	@Test
	public void shouldGetLocaleFromInitParameterWhenNotFoundInServletContext() {
		when(servletContext.getInitParameter(Config.FMT_LOCALE)).thenReturn(PT_BR.toString());
		when(request.getLocale()).thenReturn(Locale.ENGLISH);
		assumeThat(Locale.getDefault(), is(Locale.ENGLISH));

		assertThat(localization.getLocale(), equalTo(PT_BR));
	}

	@Test
	public void shouldGetLocaleFromRequestLocaleWhenNotFoundUnderAnyOtherScope() {
		when(request.getLocale()).thenReturn(PT_BR);
		assumeThat(Locale.getDefault(), is(Locale.ENGLISH));

		assertThat(localization.getLocale(), equalTo(PT_BR));
	}

	@Test
	public void findLocaleFromDefaultWhenNotFoundInAnyOtherScope() {
		assumeThat(Locale.getDefault(), is(Locale.ENGLISH));
		assertThat(localization.getLocale(), equalTo(Locale.ENGLISH));
	}

	@Test
	public void parseLocaleWithLanguage() {
		when(servletContext.getInitParameter(Config.FMT_LOCALE)).thenReturn("pt");
		assertThat(localization.getLocale().getLanguage(), equalTo("pt"));
	}

	@Test
	public void parseLocaleWithLanguageAndCountry() {
		when(servletContext.getInitParameter(Config.FMT_LOCALE)).thenReturn("pt_BR");
		assertThat(localization.getLocale().getLanguage(), equalTo("pt"));
		assertThat(localization.getLocale().getCountry(), equalTo("BR"));
	}

	@Test
	public void parseLocaleWithLanguageAndCountryAndVariant() {
		when(servletContext.getInitParameter(Config.FMT_LOCALE)).thenReturn("pt_BR_POSIX");
		assertThat(localization.getLocale().getLanguage(), equalTo("pt"));
		assertThat(localization.getLocale().getCountry(), equalTo("BR"));
		assertThat(localization.getLocale().getVariant(), equalTo("POSIX"));
	}
}
