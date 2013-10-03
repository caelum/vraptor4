package br.com.caelum.vraptor.serialization.xstream;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.inject.Instance;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;

import br.com.caelum.vraptor.interceptor.DefaultTypeNameExtractor;
import br.com.caelum.vraptor.util.test.MockInstanceImpl;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.SingleValueConverter;

/**
 * testing the same cases as {@link XStreamXMLSerializationTest}
 * but using an arbitrary {@link XStream} implementation, not the {@link VRaptorXStream}.
 * @author lucascs
 *
 */
public class XStreamSerializerTest extends XStreamXMLSerializationTest {

	@Override
	@Before
	public void setup() throws Exception {
		this.stream = new ByteArrayOutputStream();

		HttpServletResponse response = mock(HttpServletResponse.class);
		when(response.getWriter()).thenReturn(new PrintWriter(stream));

		List<Converter> converters = new ArrayList<>();
		converters.add(new CalendarConverter());

		final DefaultTypeNameExtractor extractor = new DefaultTypeNameExtractor();

		Instance<Converter> convertersInst = new MockInstanceImpl<>(converters);
		Instance<SingleValueConverter> singleValueConverters = new MockInstanceImpl<>();
		XStreamConverters xStreamConverters = new XStreamConverters(convertersInst, singleValueConverters);
		this.serialization = new XStreamXMLSerialization(response, new XStreamBuilderImpl(xStreamConverters, extractor));
	}
}