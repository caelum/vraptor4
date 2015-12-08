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
package br.com.caelum.vraptor.serialization.gson;

import static br.com.caelum.vraptor.serialization.JSONSerialization.ENVIRONMENT_INDENTED_KEY;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ForwardingCollection;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.annotations.Since;

import br.com.caelum.vraptor.core.DefaultReflectionProvider;
import br.com.caelum.vraptor.environment.Environment;
import br.com.caelum.vraptor.interceptor.DefaultTypeNameExtractor;
import br.com.caelum.vraptor.serialization.JSONPSerialization;
import br.com.caelum.vraptor.serialization.JSONSerialization;
import br.com.caelum.vraptor.serialization.Serializee;
import br.com.caelum.vraptor.serialization.SkipSerialization;
import br.com.caelum.vraptor.util.test.MockInstanceImpl;

public class GsonJSONSerializationTest {

	private GsonJSONSerialization serialization;
	private ByteArrayOutputStream stream;
	private HttpServletResponse response;
	private DefaultTypeNameExtractor extractor;
	private Environment environment;
	private GsonSerializerBuilder builder;

	@Before
	public void setup() throws Exception {
		TimeZone.setDefault(TimeZone.getTimeZone("GMT-0300"));
		
		stream = new ByteArrayOutputStream();

		response = mock(HttpServletResponse.class);
		when(response.getWriter()).thenReturn(new AlwaysFlushWriter(stream));
		extractor = new DefaultTypeNameExtractor();
		environment = mock(Environment.class);

		List<JsonSerializer<?>> jsonSerializers = new ArrayList<>();
		List<JsonDeserializer<?>> jsonDeserializers = new ArrayList<>();
		jsonSerializers.add(new CalendarGsonConverter());
		jsonSerializers.add(new DateGsonConverter());
		jsonSerializers.add(new CollectionSerializer());
		jsonSerializers.add(new EnumSerializer());

		builder = new GsonBuilderWrapper(new MockInstanceImpl<>(jsonSerializers), new MockInstanceImpl<>(jsonDeserializers), 
				new Serializee(new DefaultReflectionProvider()), new DefaultReflectionProvider());
		serialization = new GsonJSONSerialization(response, extractor, builder, environment, new DefaultReflectionProvider());
	}
	
	private static class AlwaysFlushWriter extends PrintWriter {

		public AlwaysFlushWriter(OutputStream out) {
			super(out);
		}
		
		@Override
		public void write(String s) {
			super.write(s);
			super.flush();
		}
		
	}
	
	@SkipSerialization
	public static class UserPrivateInfo {
		String cpf;
		
		String phone;
		
		public UserPrivateInfo(String cpf, String phone) {
			this.cpf = cpf;
			this.phone = phone;
		}
	}
	
	public static class User {
		String login;
		
		@SkipSerialization
		String password;
		
		UserPrivateInfo info;
		
		public User(String login, String password, UserPrivateInfo info) {
			this.login = login;
			this.password = password;
			this.info = info;
		}
	}

	public static class Address {
		@Since(1.0)
		String street;

		String city;

		public Address(String street) {
			this.street = street;
		}

		public Address(String street, String city) {
			this.street = street;
			this.city = city;
		}
	}

	public static class Client {
		@Since(1.0)
		String name;

		@Since(1.0)
		Address address;

		Calendar included;

		public Client(String name) {
			this.name = name;
		}

		public Client(String name, Address address) {
			this.name = name;
			this.address = address;
		}
	}

	public static class Item {
		String name;

		double price;

		public Item(String name, double price) {
			this.name = name;
			this.price = price;
		}
	}

	public static class Order {
		Client client;

		double price;

		String comments;

		List<Item> items;

		public Order(Client client, double price, String comments, Item... items) {
			this.client = client;
			this.price = price;
			this.comments = comments;
			this.items = Arrays.asList(items);
		}

		public String nice() {
			return "nice output";
		}

	}

	public static class AdvancedOrder extends Order {

		@SuppressWarnings("unused")
		private final String notes;

		public AdvancedOrder(Client client, double price, String comments, String notes) {
			super(client, price, comments);
			this.notes = notes;
		}

	}

	public static class GenericWrapper<T> {

		Collection<T> entityList;

		Integer total;

		public GenericWrapper(Collection<T> entityList, Integer total) {
			this.entityList = entityList;
			this.total = total;
		}

	}

	public static class ClientAddressExclusion implements ExclusionStrategy {

		@Override
		public boolean shouldSkipField(FieldAttributes f) {
			return f.getName().equals("address");
		}

		@Override
		public boolean shouldSkipClass(Class<?> clazz) {
			return false;
		}

	}

	@Test
	public void shouldSerializeGenericClass() {
		String expectedResult = "{\"genericWrapper\":{\"entityList\":[{\"name\":\"washington botelho\"},{\"name\":\"washington botelho\"}],\"total\":2}}";

		Collection<Client> entityList = new ArrayList<>();
		entityList.add(new Client("washington botelho"));
		entityList.add(new Client("washington botelho"));

		GenericWrapper<Client> wrapper = new GenericWrapper<>(entityList, entityList.size());

		serialization.from(wrapper).include("entityList").serialize();

		assertThat(result(), is(equalTo(expectedResult)));
	}

	@Test
	public void shouldSerializeAllBasicFields() {
		String expectedResult = "{\"order\":{\"price\":15.0,\"comments\":\"pack it nicely, please\"}}";
		Order order = new Order(new Client("guilherme silveira"), 15.0, "pack it nicely, please");
		serialization.from(order).serialize();
		assertThat(result(), is(equalTo(expectedResult)));
	}

	@Test
	public void shouldSerializeAllBasicFieldsIndented() {
		String expectedResult = "{\n  \"order\": {\n    \"price\": 15.0,\n    \"comments\": \"pack it nicely, please\"\n  }\n}";
		Order order = new Order(new Client("guilherme silveira"), 15.0, "pack it nicely, please");
		serialization.indented().from(order).serialize();
		assertThat(result(), is(equalTo(expectedResult)));
	}

	@Test
	public void shouldIndentedWhenEnvironmentReturnsTrue() {
		when(environment.supports(ENVIRONMENT_INDENTED_KEY)).thenReturn(true);
		serialization.init();

		String expectedResult = "{\n  \"order\": {\n    \"price\": 15.0,\n    \"comments\": \"pack it nicely, please\"\n  }\n}";
		Order order = new Order(new Client("guilherme silveira"), 15.0, "pack it nicely, please");
		serialization.from(order).serialize();
		assertThat(result(), is(equalTo(expectedResult)));
	}

	@Test
	public void shouldNotIndentedWhenEnvironmentReturnsFalse() {
		when(environment.supports(ENVIRONMENT_INDENTED_KEY)).thenReturn(false);
		serialization.init();

		String expectedResult = "{\"order\":{\"price\":15.0,\"comments\":\"pack it nicely, please\"}}";
		Order order = new Order(new Client("guilherme silveira"), 15.0, "pack it nicely, please");
		serialization.from(order).serialize();
		assertThat(result(), is(equalTo(expectedResult)));
	}

	@Test
	public void shouldUseAlias() {
		String expectedResult = "{\"customOrder\":{\"price\":15.0,\"comments\":\"pack it nicely, please\"}}";
		Order order = new Order(new Client("guilherme silveira"), 15.0, "pack it nicely, please");
		serialization.from(order, "customOrder").serialize();
		assertThat(result(), is(equalTo(expectedResult)));
	}

	public static enum Type {
		basic, advanced
	}

	class BasicOrder extends Order {
		public BasicOrder(Client client, double price, String comments, Type type) {
			super(client, price, comments);
			this.type = type;
		}

		@SuppressWarnings("unused")
		private final Type type;
	}

	@Test
	public void shouldSerializeEnumFields() {
		// String expectedResult =
		// "<basicOrder><type>basic</type><price>15.0</price><comments>pack it nicely, please</comments></basicOrder>";
		Order order = new BasicOrder(new Client("guilherme silveira"), 15.0, "pack it nicely, please",
				Type.basic);
		serialization.from(order).serialize();
		String result = result();
		assertThat(result, containsString("\"type\":\"basic\""));
	}

	@Test
	public void shouldSerializeCollection() {
		String expectedResult = "{\"price\":15.0,\"comments\":\"pack it nicely, please\"}";
		expectedResult += "," + expectedResult;
		expectedResult = "{\"list\":[" + expectedResult + "]}";

		Order order = new Order(new Client("guilherme silveira"), 15.0, "pack it nicely, please");
		serialization.from(Arrays.asList(order, order)).serialize();
		assertThat(result(), is(equalTo(expectedResult)));
	}

	@Test
	public void shouldSerializeCollectionWithPrefixTag() {
		String expectedResult = "{\"price\":15.0,\"comments\":\"pack it nicely, please\"}";
		expectedResult += "," + expectedResult;
		expectedResult = "{\"orders\":[" + expectedResult + "]}";
		Order order = new Order(new Client("guilherme silveira"), 15.0, "pack it nicely, please");
		serialization.from(Arrays.asList(order, order), "orders").serialize();
		assertThat(result(), is(equalTo(expectedResult)));
	}

	@Test
	public void shouldExcludeNonPrimitiveFieldsFromACollection() {
		Order order = new Order(new Client("guilherme silveira"), 15.0, "pack it nicely, please", new Item(
				"name", 12.99));
		serialization.from(Arrays.asList(order, order), "orders").exclude("price").serialize();

		assertThat(result(), not(containsString("\"items\"")));
		assertThat(result(), not(containsString("name")));
		assertThat(result(), not(containsString("\"price\"")));
		assertThat(result(), not(containsString("12.99")));
		assertThat(result(), not(containsString("15.0")));
	}

	@Test
	public void shouldExcludeOnlyOmmitedFields() {
		User user = new User("caelum", "pwd12345",
			new UserPrivateInfo("123.456.789-00", "+55 (11) 1111-1111"));
		serialization.from(user).recursive().serialize();

		assertThat(result(), not(containsString("\"pwd12345\"")));
		assertThat(result(), not(containsString("password")));
		assertThat(result(), containsString("\"caelum\""));
		assertThat(result(), containsString("login"));
	}
	
	@Test
	public void shouldExcludeOmmitedClasses() {
		User user = new User("caelum", "pwd12345",
			new UserPrivateInfo("123.456.789-00", "+55 (11) 1111-1111"));
		serialization.from(user).recursive().serialize();

		assertThat(result(), not(containsString("info")));
		assertThat(result(), not(containsString("cpf")));
		assertThat(result(), not(containsString("phone")));
	}

	static class WithAdvanced {
		AdvancedOrder order;
	}

	@Test
	public void shouldSerializeParentFields() {
		// String expectedResult =
		// "<advancedOrder><notes>complex package</notes><price>15.0</price><comments>pack it nicely, please</comments></advancedOrder>";
		Order order = new AdvancedOrder(null, 15.0, "pack it nicely, please", "complex package");
		serialization.from(order).serialize();
		assertThat(result(), containsString("\"notes\":\"complex package\""));
	}

	@Test
	public void shouldExcludeNonPrimitiveParentFields() {
		// String expectedResult =
		// "<advancedOrder><notes>complex package</notes><price>15.0</price><comments>pack it nicely, please</comments></advancedOrder>";
		WithAdvanced advanced = new WithAdvanced();
		advanced.order = new AdvancedOrder(new Client("john"), 15.0, "pack it nicely, please",
				"complex package");
		serialization.from(advanced).include("order").serialize();
		assertThat(result(), not(containsString("\"client\"")));
	}

	@Test
	public void shouldExcludeParentFields() {
		Order order = new AdvancedOrder(null, 15.0, "pack it nicely, please", "complex package");
		serialization.from(order).exclude("comments").serialize();
		assertThat(result(), not(containsString("\"comments\"")));
	}

	@Test
	public void shouldOptionallyExcludeFields() {
		String expectedResult = "{\"order\":{\"comments\":\"pack it nicely, please\"}}";
		Order order = new Order(new Client("guilherme silveira"), 15.0, "pack it nicely, please");
		serialization.from(order).exclude("price").serialize();
		assertThat(result(), is(equalTo(expectedResult)));
	}

	@Test
	public void shouldOptionallyIncludeFieldAndNotItsNonPrimitiveFields() {
		// String expectedResult =
		// "<order><client><name>guilherme silveira</name> </client>  <price>15.0</price><comments>pack it nicely, please</comments></order>";
		Order order = new Order(new Client("guilherme silveira", new Address("R. Vergueiro")), 15.0,
				"pack it nicely, please");
		serialization.from(order).include("client").serialize();
		assertThat(result(), containsString("\"name\":\"guilherme silveira\""));
		assertThat(result(), not(containsString("R. Vergueiro")));
	}

	@Test
	public void shouldOptionallyIncludeChildField() {
		// String expectedResult =
		// "<order><client><name>guilherme silveira</name> </client>  <price>15.0</price><comments>pack it nicely, please</comments></order>";
		Order order = new Order(new Client("guilherme silveira", new Address("R. Vergueiro")), 15.0,
				"pack it nicely, please");
		serialization.from(order).include("client", "client.address").serialize();
		assertThat(result(), containsString("\"street\":\"R. Vergueiro\""));
	}

	@Test
	public void shouldOptionallyExcludeChildField() {
		// String expectedResult =
		// "<order><client></client>  <price>15.0</price><comments>pack it nicely, please</comments></order>";
		Order order = new Order(new Client("guilherme silveira"), 15.0, "pack it nicely, please");
		serialization.from(order).include("client").exclude("client.name").serialize();
		assertThat(result(), containsString("\"client\""));
		assertThat(result(), not(containsString("guilherme silveira")));
	}

	@Test
	public void shouldOptionallyIncludeListChildFields() {
		// String expectedResult =
		// "<order><client></client>  <price>15.0</price><comments>pack it nicely, please</comments></order>";
		Order order = new Order(new Client("guilherme silveira"), 15.0, "pack it nicely, please", new Item(
				"any item", 12.99));
		serialization.from(order).include("items").serialize();
		assertThat(result(), containsString("\"items\""));
		assertThat(result(), containsString("\"name\":\"any item\""));
		assertThat(result(), containsString("\"price\":12.99"));
	}
	
	@Test
	public void shouldExcludeAllPrimitiveFieldsInACollection() {
		String expectedResult = "{\"list\":[{},{}]}";
		List<Order> orders = new ArrayList<>();
		orders.add(new Order(new Client("nykolas lima"), 15.0, "gift bags, please"));
		orders.add(new Order(new Client("Rafael Dipold"), 15.0, "gift bags, please"));
		serialization.from(orders).excludeAll().serialize();
		assertThat(result(), is(equalTo(expectedResult)));
	}	

	@Test
	public void shouldOptionallyExcludeFieldsFromIncludedListChildFields() {
		// String expectedResult =
		// "<order><client></client>  <price>15.0</price><comments>pack it nicely, please</comments></order>";
		Order order = new Order(new Client("guilherme silveira"), 15.0, "pack it nicely, please", new Item(
				"any item", 12.99));
		serialization.from(order).include("items").exclude("items.price").serialize();
		assertThat(result(), containsString("\"items\""));
		assertThat(result(), containsString("\"name\":\"any item\""));
		assertThat(result(), not(containsString("12.99")));
	}

	@Test
	public void shouldOptionallyRemoveRoot() {
		Order order = new Order(new Client("guilherme silveira"), 15.0, "pack it nicely, please", new Item(
				"any item", 12.99));
		serialization.withoutRoot().from(order).include("items").exclude("items.price").serialize();
		assertThat(result(), containsString("\"items\""));
		assertThat(result(), containsString("\"name\":\"any item\""));
		assertThat(result(), not(containsString("12.99")));
		assertThat(result(), not(containsString("\"order\":")));
	}

	@Test
	public void shouldOptionallyRemoveRootIndented() {
		String expected = "{\n  \"price\": 15.0,\n  \"comments\": \"pack it nicely, please\",\n  \"items\": [\n    {\n      \"name\": \"any item\"\n    }\n  ]\n}";
		Order order = new Order(new Client("guilherme silveira"), 15.0, "pack it nicely, please", new Item(
				"any item", 12.99));
		serialization.indented().withoutRoot().from(order).include("items").exclude("items.price").serialize();
		assertThat(result(), equalTo(expected));
	}

	private String result() {
		try {
			stream.flush();
			return new String(stream.toByteArray());
		} catch (IOException e) {
			throw new RuntimeException("Error flushing stream", e);
		}
	}

	static class MyCollection extends ForwardingCollection<Order> {
		@Override
		protected Collection<Order> delegate() {
			return Arrays.asList(new Order(new Client("client"), 12.22, "hoay"));
		}

	}

	static class CollectionSerializer implements JsonSerializer<MyCollection> {
		@Override
		public JsonElement serialize(MyCollection myColl, java.lang.reflect.Type typeOfSrc,
				JsonSerializationContext context) {
			return new JsonParser().parse("[testing]").getAsJsonArray();
		}
	}
	
	//Expect that a ParameterizedType should be registered
	static class EnumSerializer implements JsonSerializer<Enum<?>> {
		@Override public JsonElement serialize(Enum<?> src, java.lang.reflect.Type typeOfSrc, JsonSerializationContext context) {
			return new JsonPrimitive(src.name());
		}
	}
	
	@Test
	public void shouldUseCollectionConverterWhenItExists() {
		String expectedResult = "[\"testing\"]";

		serialization.withoutRoot().from(new MyCollection()).serialize();
		assertThat(result(), is(equalTo(expectedResult)));
	}

	@Test
	public void shouldSerializeCalendarTimeWithISO8601() {
		Client c = new Client("renan");
		c.included = new GregorianCalendar(2012, 8, 3, 1, 5, 9);
		c.included.setTimeZone(TimeZone.getTimeZone("GMT-0300"));

		serialization.from(c).serialize();
		String result = result();

		String expectedResult = "{\"client\":{\"name\":\"renan\",\"included\":\"2012-09-03T01:05:09-03:00\"}}";

		assertThat(result, is(equalTo(expectedResult)));
	}
	
	@Test
	public void shouldSerializeDateWithISO8601() {
		Date date = new GregorianCalendar(1988, 0, 25, 1, 30, 15).getTime();
		serialization.from(date).serialize();
		String expectedResult = "{\"date\":\"1988-01-25T01:30:15-0300\"}";
		assertThat(result(), is(equalTo(expectedResult)));
	}

	@Test
	public void shouldExcludeAllPrimitiveFields() {
		String expectedResult = "{\"order\":{}}";
		Order order = new Order(new Client("nykolas lima"), 15.0, "gift bags, please");
		serialization.from(order).excludeAll().serialize();
		assertThat(result(), is(equalTo(expectedResult)));
	}

	@Test
	public void shouldExcludeAllPrimitiveParentFields() {
		String expectedResult = "{\"advancedOrder\":{}}";
		Order order = new AdvancedOrder(null, 15.0, "pack it nicely, please", "complex package");
		serialization.from(order).excludeAll().serialize();
		assertThat(result(), is(equalTo(expectedResult)));
	}

	@Test
	public void shouldExcludeAllThanIncludeAndSerialize() {
		String expectedResult = "{\"order\":{\"price\":15.0}}";
		Order order = new Order(new Client("nykolas lima"), 15.0, "gift bags, please");
		serialization.from(order).excludeAll().include("price").serialize();
		assertThat(result(), is(equalTo(expectedResult)));
	}

	@Test
	public void shouldSerializeWithCallback() {
		JSONPSerialization serialization = new GsonJSONPSerialization(response, extractor, builder, environment,
				new DefaultReflectionProvider());

		String expectedResult = "calculate({\"order\":{\"price\":15.0}})";
		Order order = new Order(new Client("nykolas lima"), 15.0, "gift bags, please");
		serialization.withCallback("calculate").from(order).excludeAll().include("price").serialize();
		assertThat(result(), is(equalTo(expectedResult)));
	}

	@Test
	public void shouldSerializeVersionedJsonFieldsWithSinceAnnotation() {
		JSONSerialization serialization = new GsonJSONSerialization(response, extractor, builder, 
				environment, new DefaultReflectionProvider());

		String expectedResult = "{\"client\":{\"name\":\"adolfo eloy\"}}";
		Client client = new Client("adolfo eloy");
		serialization.version(1.0).from(client).serialize();
		assertThat(result(), is(equalTo(expectedResult)));
	}

	@Test
	public void shouldSerializeRecursivelyVersionedJsonFields() {
		JSONSerialization serialization = new GsonJSONSerialization(response, extractor, builder, 
				environment, new DefaultReflectionProvider());

		String expectedResult = "{\"client\":{\"name\":\"adolfo eloy\",\"address\":{\"street\":\"antonio agu\",\"city\":\"osasco\"}}}";

		Client client = new Client("adolfo eloy", new Address("antonio agu", "osasco"));
		serialization.version(1.0).from(client).recursive().serialize();
		assertThat(result(), is(equalTo(expectedResult)));
	}
	
	@Test
	public void shouldSerializeNullfieldswhenRequested(){
		Address address = new Address("Alameda street", null);
		serialization.serializeNulls().from(address).serialize();
		
		String expectedResult = "{\"address\":{\"street\":\"Alameda street\",\"city\":null}}";
		assertThat(result(), is(equalTo(expectedResult)));
	}
	
	@Test
	public void shouldNotSerializeNullFieldsByDefault(){
		Address address = new Address("Alameda street", null);
		serialization.from(address).serialize();
		
		String expectedResult = "{\"address\":{\"street\":\"Alameda street\"}}";
		assertThat(result(), is(equalTo(expectedResult)));
	}
	
}
