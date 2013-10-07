package br.com.caelum.vraptor.core;

import static javax.servlet.jsp.jstl.core.Config.FMT_LOCALE;
import static javax.servlet.jsp.jstl.core.Config.FMT_LOCALIZATION_CONTEXT;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ListResourceBundle;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.jstl.fmt.LocalizationContext;

import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.http.MutableRequest;

/**
 * Test class for {@link JstlLocalization}.
 * 
 * @author Otávio Scherer Garcia
 */
public class JstlLocalizationTest {

	static final Locale PT_BR = new Locale("pt", "BR");

	private JstlLocalization localization;

	private MutableRequest request;
	private ServletContext servletContext;
	private HttpSession session;

	@Before
	public void setUp() {
		request = mock(MutableRequest.class);
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

		when(request.getSession()).thenReturn(session);
		when(request.getServletContext()).thenReturn(servletContext);
		
		Locale.setDefault(Locale.ENGLISH);
	}

	@Test
	public void findLocaleFromRequest() {
		when(request.getAttribute(FMT_LOCALE + ".request")).thenReturn(PT_BR);
		assertThat(localization.getLocale(), equalTo(PT_BR));
	}

	@Test
	public void findLocaleFromSession() {
		when(session.getAttribute(FMT_LOCALE + ".session")).thenReturn(PT_BR);
		assertThat(localization.getLocale(), equalTo(PT_BR));
	}

	@Test
	public void findLocaleFromServletContext() {
		when(servletContext.getInitParameter(FMT_LOCALE)).thenReturn(PT_BR.toString());
		assertThat(localization.getLocale(), equalTo(PT_BR));
	}

	@Test
	public void findLocaleFromRequestLocale() {
		when(request.getLocale()).thenReturn(PT_BR);
		assertThat(localization.getLocale(), equalTo(PT_BR));
	}

	@Test
	public void findLocaleFromDefaultWhenNotFoundInAnyScope() {
		assertThat(localization.getLocale(), equalTo(Locale.ENGLISH));
	}

	@Test
	public void testLocaleWithSessionNotRequest() {
		when(request.getAttribute(FMT_LOCALE + ".request")).thenReturn(PT_BR);
		when(session.getAttribute(FMT_LOCALE + ".session")).thenReturn(Locale.ENGLISH);
		assertThat(localization.getLocale(), equalTo(PT_BR));
	}
}
