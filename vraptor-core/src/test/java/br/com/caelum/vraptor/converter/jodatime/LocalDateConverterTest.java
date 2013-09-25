package br.com.caelum.vraptor.converter.jodatime;

import static br.com.caelum.vraptor.VRaptorMatchers.hasMessage;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.when;

import java.util.Locale;

import javax.servlet.ServletContext;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.com.caelum.vraptor.converter.ConversionException;
import br.com.caelum.vraptor.http.MutableRequest;

/**
 * Tests to {@link LocalDateConverter}.
 */
public class LocalDateConverterTest {

	private LocalDateConverter converter;
	private @Mock MutableRequest request;
	private @Mock ServletContext context;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);

		when(request.getServletContext()).thenReturn(context);

		converter = new LocalDateConverter(new Locale("pt", "BR"));
	}

	@Test
	public void shouldBeAbleToConvert() {
		assertThat(converter.convert("05/06/2010", LocalDate.class),
				is(equalTo(new LocalDate(2010, 6, 5))));
	}

	@Test
	public void shouldBeAbleToConvertEmpty() {
		assertThat(converter.convert("", LocalDate.class), is(nullValue()));
	}

	@Test
	public void shouldBeAbleToConvertNull() {
		assertThat(converter.convert(null, LocalDate.class), is(nullValue()));
	}

	@Test
	public void shouldThrowExceptionWhenUnableToParse() {
		try {
			converter.convert("a,10/06/2008/a/b/c", LocalDate.class);
		} catch (ConversionException e) {
			assertThat(e.getValidationMessage(), hasMessage("a,10/06/2008/a/b/c is not a valid date."));
		}
	}
}