package br.com.caelum.vraptor.serialization;

import static br.com.caelum.vraptor.view.Results.json;
import static br.com.caelum.vraptor.view.Results.xml;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.interceptor.DefaultTypeNameExtractor;
import br.com.caelum.vraptor.ioc.Container;
import br.com.caelum.vraptor.serialization.gson.CalendarSerializer;
import br.com.caelum.vraptor.serialization.gson.GsonJSONSerialization;
import br.com.caelum.vraptor.serialization.gson.MessageSerializer;
import br.com.caelum.vraptor.serialization.gson.GsonSerializerBuilder;
import br.com.caelum.vraptor.serialization.xstream.MessageConverter;
import br.com.caelum.vraptor.serialization.xstream.XStreamBuilder;
import br.com.caelum.vraptor.serialization.xstream.XStreamBuilderFactory;
import br.com.caelum.vraptor.serialization.xstream.XStreamXMLSerialization;
import br.com.caelum.vraptor.util.test.MockInstanceImpl;
import br.com.caelum.vraptor.validator.SingletonResourceBundle;

import com.google.gson.JsonSerializer;

public class I18nMessageSerializationTest {
	private I18nMessageSerialization serialization;
	private ByteArrayOutputStream stream;

	@Before
	public void setup() throws Exception {
		stream = new ByteArrayOutputStream();

		HttpServletResponse response = mock(HttpServletResponse.class);
		when(response.getWriter()).thenReturn(new PrintWriter(stream));
		DefaultTypeNameExtractor extractor = new DefaultTypeNameExtractor();
		XStreamBuilder builder = XStreamBuilderFactory.cleanInstance(new MessageConverter());
		XStreamXMLSerialization xmlSerialization = new XStreamXMLSerialization(response, builder);

		List<JsonSerializer<?>> adapters = new ArrayList<>();
		adapters.add(new CalendarSerializer());
		adapters.add(new MessageSerializer());

		GsonSerializerBuilder gsonBuilder =  new GsonSerializerBuilder(new MockInstanceImpl<>(adapters));
		GsonJSONSerialization jsonSerialization = new GsonJSONSerialization(response, extractor, gsonBuilder);

		Container container = mock(Container.class);
		when(container.instanceFor(JSONSerialization.class)).thenReturn(jsonSerialization);
		when(container.instanceFor(XMLSerialization.class)).thenReturn(xmlSerialization);

		ResourceBundle bundle = new SingletonResourceBundle("message.cat", "Just another {0} in {1}");
		serialization = new I18nMessageSerialization(container , bundle);
	}

	@Test
	public void shouldCallJsonSerialization() {
		String expectedResult = "{\"message\":{\"category\":\"success\",\"message\":\"Just another {0} in {1}\"}}";
		serialization.from("success", "message.cat").as(json());
		assertThat(result(), is(equalTo(expectedResult)));
	}

	@Test
	public void shouldCallXStreamXmlSerialization() {
		String expectedResult = "<message>\n" +
								"  <message>Just another {0} in {1}</message>\n" +
								"  <category>success</category>\n" +
								"</message>";
		serialization.from("success", "message.cat").as(xml());
		assertThat(result(), is(equalTo(expectedResult)));
	}

	@Test
	public void shouldCallXStreamSerializationWithParams() {
		String expectedResult = "<message>\n" +
				"  <message>Just another object in memory</message>\n" +
				"  <category>success</category>\n" +
				"</message>";
		Object[] params = {"object", "memory"};
		serialization.from("success", "message.cat", params).as(xml());
		assertThat(result(), is(equalTo(expectedResult)));
	}


	private String result() {
		return new String(stream.toByteArray());
	}

}
