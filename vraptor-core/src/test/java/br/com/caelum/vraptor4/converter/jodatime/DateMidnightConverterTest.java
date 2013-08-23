package br.com.caelum.vraptor4.converter.jodatime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ResourceBundle;

import javax.servlet.FilterChain;
import javax.servlet.ServletContext;

import org.joda.time.DateMidnight;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.com.caelum.vraptor4.converter.ConversionError;
import br.com.caelum.vraptor4.core.JstlLocalization;
import br.com.caelum.vraptor4.core.RequestInfo;
import br.com.caelum.vraptor4.http.MutableRequest;

/**
 * Tests to {@link DateMidnightConverter}.
 */
public class DateMidnightConverterTest {
	
	private DateMidnightConverter converter;
	private @Mock MutableRequest request;
	private @Mock ServletContext context;
	private @Mock ResourceBundle bundle;
	private @Mock JstlLocalization jstlLocalization;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		
		bundle = ResourceBundle.getBundle("messages");
		FilterChain chain = mock(FilterChain.class);

		final RequestInfo webRequest = new RequestInfo(context, chain, request, null);
        jstlLocalization = new JstlLocalization(webRequest);

		converter = new DateMidnightConverter(jstlLocalization);
	}

	@Test
	public void shouldBeAbleToConvert() {
		when(request.getAttribute("javax.servlet.jsp.jstl.fmt.locale.request"))
				.thenReturn("pt_br");

		assertThat(converter.convert("05/06/2010", DateMidnight.class, bundle),
				is(equalTo(new DateMidnight(2010, 6, 5))));
	}
	
	@Test
	public void shouldBeAbleToConvertEmpty() {
		assertThat(converter.convert("", DateMidnight.class, bundle), is(nullValue()));
	}

	@Test
	public void shouldBeAbleToConvertNull() {
		assertThat(converter.convert(null, DateMidnight.class, bundle), is(nullValue()));
	}

	@Test
	public void shouldThrowExceptionWhenUnableToParse() {
		when(request.getAttribute("javax.servlet.jsp.jstl.fmt.locale.request"))
			.thenReturn("pt_br");
		
		try {
			converter.convert("a,10/06/2008/a/b/c", DateMidnight.class, bundle);
		} catch (ConversionError e) {
			assertThat(e.getMessage(), is(equalTo("a,10/06/2008/a/b/c is not a valid datetime.")));
		}
	}
}