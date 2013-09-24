package br.com.caelum.vraptor.converter.jodatime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.when;

import java.util.Locale;
import java.util.ResourceBundle;

import javax.servlet.ServletContext;

import org.joda.time.LocalDateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.com.caelum.vraptor.converter.ConversionException;
import br.com.caelum.vraptor.http.MutableRequest;

/**
 * Tests to {@link LocalDateTimeConverter}.
 */
public class LocalDateTimeConverterTest {

	private LocalDateTimeConverter converter;
	private @Mock MutableRequest request;
	private @Mock ServletContext context;
	private @Mock ResourceBundle bundle;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);

		bundle = ResourceBundle.getBundle("messages");

		when(request.getServletContext()).thenReturn(context);

		converter = new LocalDateTimeConverter(new Locale("pt", "BR"));
	}

	@Test
	public void shouldBeAbleToConvert() {
		assertThat(converter.convert("05/06/2010 3:38", LocalDateTime.class, bundle),
				is(equalTo(new LocalDateTime(2010, 6, 5, 3, 38))));
	}

	@Test
	public void shouldBeAbleToConvertEmpty() {
		assertThat(converter.convert("", LocalDateTime.class, bundle), is(nullValue()));
	}

	@Test
	public void shouldBeAbleToConvertNull() {
		assertThat(converter.convert(null, LocalDateTime.class, bundle), is(nullValue()));
	}

	@Test
	public void shouldThrowExceptionWhenUnableToParse() {
		try {
			converter.convert("a,10/06/2008/a/b/c", LocalDateTime.class, bundle);
		} catch (ConversionException e) {
			assertThat(e.getMessage(), is(equalTo("a,10/06/2008/a/b/c is not a valid datetime.")));
		}
	}
}