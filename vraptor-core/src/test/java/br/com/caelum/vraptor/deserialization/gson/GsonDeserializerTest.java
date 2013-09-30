package br.com.caelum.vraptor.deserialization.gson;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;

import net.vidageek.mirror.dsl.Mirror;

import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.controller.BeanClass;
import br.com.caelum.vraptor.controller.ControllerMethod;
import br.com.caelum.vraptor.controller.DefaultBeanClass;
import br.com.caelum.vraptor.controller.DefaultControllerMethod;
import br.com.caelum.vraptor.http.ParameterNameProvider;
import br.com.caelum.vraptor.ioc.cdi.FakeInstanceImpl;
import br.com.caelum.vraptor.view.GenericController;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

public class GsonDeserializerTest {

	private GsonDeserialization deserializer;
	private ControllerMethod bark;
	private ParameterNameProvider provider;
	private ControllerMethod jump;
	private ControllerMethod woof;
	private ControllerMethod dropDead;
	private HttpServletRequest request;

	@Before
	public void setUp() throws Exception {
		provider = mock(ParameterNameProvider.class);
		request = mock(HttpServletRequest.class);

		List<JsonDeserializer<?>> adapters = new ArrayList<>();
		adapters.add(new CalendarDeserializer());

		deserializer = new GsonDeserialization(provider, new FakeInstanceImpl<>(adapters), request);
		BeanClass controllerClass = new DefaultBeanClass(DogController.class);

		woof = new DefaultControllerMethod(controllerClass, DogController.class.getDeclaredMethod("woof"));
		bark = new DefaultControllerMethod(controllerClass, DogController.class.getDeclaredMethod("bark", Dog.class));
		jump = new DefaultControllerMethod(controllerClass, DogController.class.getDeclaredMethod("jump", Dog.class,
				Integer.class));
		dropDead = new DefaultControllerMethod(controllerClass, DogController.class.getDeclaredMethod("dropDead",
				Integer.class, Dog.class));
	}

	static class Dog {
		private String name;
		private Integer age;
		private Calendar birthday;
	}

	static class DogController {

		public void woof() {
		}

		public void bark(Dog dog) {
		}

		public void jump(Dog dog, Integer times) {
		}

		public void dropDead(Integer times, Dog dog) {
		}

	}

	private class DogDeserializer implements JsonDeserializer<Dog> {

		public Dog deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
				throws JsonParseException {
			Dog dog = new Dog();
			dog.name = "Renan";
			dog.age = 25;

			return dog;
		}

	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotAcceptMethodsWithoutArguments() throws Exception {
		deserializer.deserialize(new ByteArrayInputStream(new byte[0]), woof);
	}

	@Test
	public void shouldBeAbleToDeserializeADog() throws Exception {
		InputStream stream = new ByteArrayInputStream("{'dog':{'name':'Brutus','age':7}}".getBytes());

		when(provider.parameterNamesFor(bark.getMethod())).thenReturn(new String[] { "dog" });

		Object[] deserialized = deserializer.deserialize(stream, bark);

		assertThat(deserialized.length, is(1));
		assertThat(deserialized[0], is(instanceOf(Dog.class)));
		Dog dog = (Dog) deserialized[0];
		assertThat(dog.name, is("Brutus"));
		assertThat(dog.age, is(7));
	}

	@Test
	public void shouldBeAbleToDeserializeADogWithoutRoot() throws Exception {
		InputStream stream = new ByteArrayInputStream("{'name':'Brutus','age':7}".getBytes());

		when(provider.parameterNamesFor(bark.getMethod())).thenReturn(new String[] { "dog" });

		Object[] deserialized = deserializer.deserialize(stream, bark);

		assertThat(deserialized.length, is(1));
		assertThat(deserialized[0], is(instanceOf(Dog.class)));
		Dog dog = (Dog) deserialized[0];
		assertThat(dog.name, is("Brutus"));
		assertThat(dog.age, is(7));
	}

	@Test
	public void shouldBeAbleToDeserializeADogWithDeserializerAdapter() throws Exception {
		List<JsonDeserializer<?>> deserializers = new ArrayList<>();
		deserializers.add(new DogDeserializer());

		deserializer = new GsonDeserialization(provider, new FakeInstanceImpl<>(deserializers), request);

		InputStream stream = new ByteArrayInputStream("{'dog':{'name':'Renan Reis','age':'0'}}".getBytes());

		when(provider.parameterNamesFor(bark.getMethod())).thenReturn(new String[] { "dog" });
		when(provider.parameterNamesFor(bark.getMethod())).thenReturn(new String[] { "dog" });

		Object[] deserialized = deserializer.deserialize(stream, bark);

		assertThat(deserialized.length, is(1));
		assertThat(deserialized[0], is(instanceOf(Dog.class)));
		Dog dog = (Dog) deserialized[0];
		assertThat(dog.name, is("Renan"));
		assertThat(dog.age, is(25));
	}

	@Test
	public void shouldBeAbleToDeserializeADogWhenMethodHasMoreThanOneArgument() throws Exception {
		InputStream stream = new ByteArrayInputStream("{'dog':{'name':'Brutus','age':7}}".getBytes());

		when(provider.parameterNamesFor(jump.getMethod())).thenReturn(new String[] { "dog", "times" });

		Object[] deserialized = deserializer.deserialize(stream, jump);

		assertThat(deserialized.length, is(2));
		assertThat(deserialized[0], is(instanceOf(Dog.class)));
		Dog dog = (Dog) deserialized[0];
		assertThat(dog.name, is("Brutus"));
		assertThat(dog.age, is(7));
	}

	@Test
	public void shouldBeAbleToDeserializeADogWhenMethodHasMoreThanOneArgumentAndTheXmlIsTheLastOne() throws Exception {
		InputStream stream = new ByteArrayInputStream("{'dog':{'name':'Brutus','age':7}}".getBytes());

		when(provider.parameterNamesFor(dropDead.getMethod())).thenReturn(new String[] { "times", "dog" });

		Object[] deserialized = deserializer.deserialize(stream, dropDead);

		assertThat(deserialized.length, is(2));
		assertThat(deserialized[1], is(instanceOf(Dog.class)));
		Dog dog = (Dog) deserialized[1];
		assertThat(dog.name, is("Brutus"));
		assertThat(dog.age, is(7));
	}

	@Test
	public void shouldBeAbleToDeserializeADogNamedDifferently() throws Exception {
		InputStream stream = new ByteArrayInputStream("{'pet':{'name':'Brutus','age':7}}".getBytes());

		when(provider.parameterNamesFor(bark.getMethod())).thenReturn(new String[] { "pet" });

		Object[] deserialized = deserializer.deserialize(stream, bark);

		assertThat(deserialized.length, is(1));
		assertThat(deserialized[0], is(instanceOf(Dog.class)));
		Dog dog = (Dog) deserialized[0];
		assertThat(dog.name, is("Brutus"));
		assertThat(dog.age, is(7));
	}

	@Test
	public void shouldHonorRequestHeaderAcceptCharset() throws Exception {
		InputStream stream = new ByteArrayInputStream("{'pet':{'name':'Ã§'}}".getBytes("ISO-8859-1"));
		when(provider.parameterNamesFor(bark.getMethod())).thenReturn(new String[] { "pet" });

		when(request.getHeader("Accept-Charset")).thenReturn("UTF-8,*;q=0.5");
		Object[] deserialized = deserializer.deserialize(stream, bark);

		assertThat(deserialized.length, is(1));
		assertThat(deserialized[0], is(instanceOf(Dog.class)));

		Dog dog = (Dog) deserialized[0];

		assertThat(dog.name, is("ç"));
	}

	@Test
	public void whenNoCharsetHeaderIsFoundThanAssumeItIsUTF8() throws Exception {
		InputStream stream = new ByteArrayInputStream("{'pet':{'name':'Ã§'}}".getBytes("ISO-8859-1"));
		when(provider.parameterNamesFor(bark.getMethod())).thenReturn(new String[] { "pet" });

		when(request.getHeader("Accept-Charset")).thenReturn(null);
		Object[] deserialized = deserializer.deserialize(stream, bark);

		assertThat(deserialized.length, is(1));
		assertThat(deserialized[0], is(instanceOf(Dog.class)));

		Dog dog = (Dog) deserialized[0];

		assertThat(dog.name, is("ç"));
	}

	@Test
	public void shouldByPassDeserializationWhenHasNoContent() {
		InputStream stream = new ByteArrayInputStream("".getBytes());
		when(provider.parameterNamesFor(bark.getMethod())).thenReturn(new String[] { "pet" });

		Object[] deserialized = deserializer.deserialize(stream, bark);

		assertThat(deserialized.length, is(1));
		assertThat(deserialized[0], is(nullValue()));
	}

	@Test
	public void shouldBeAbleToDeserializeADogWhenMethodHasMoreThanOneArgumentAndHasNotRoot()
			throws Exception {
		InputStream stream = new ByteArrayInputStream("{'name':'Brutus','age':7}".getBytes());

		when(provider.parameterNamesFor(jump.getMethod())).thenReturn(new String[] { "dog", "times" });

		Object[] deserialized = deserializer.deserialize(stream, jump);

		assertThat(deserialized.length, is(2));
		assertThat(deserialized[0], is(instanceOf(Dog.class)));
		Dog dog = (Dog) deserialized[0];
		assertThat(dog.name, is("Brutus"));
		assertThat(dog.age, is(7));
	}

	static class ExtGenericController extends GenericController<Dog> {

	}

	@Test
	public void shouldDeserializeFromGenericTypeOneParam() {
		InputStream stream = new ByteArrayInputStream(
				"{'entity':{'name':'Brutus','age':7,'birthday':'2013-07-23T17:14:14-03:00'}}"
						.getBytes());
		BeanClass resourceClass = new DefaultBeanClass(ExtGenericController.class);
		Method method = new Mirror().on(GenericController.class).reflect()
				.method("method").withAnyArgs();
		ControllerMethod resource = new DefaultControllerMethod(resourceClass, method);
		when(provider.parameterNamesFor(resource.getMethod())).thenReturn(new String[] { "entity" });

		Object[] deserialized = deserializer.deserialize(stream, resource);

		Dog dog = (Dog) deserialized[0];

		assertThat(dog.name, equalTo("Brutus"));
	}

	@Test
	public void shouldDeserializeFromGenericTypeTwoParams() {
		InputStream stream = new ByteArrayInputStream("{'entity':{'name':'Brutus','age':7,'birthday':'2013-07-23T17:14:14-03:00'}, 'param': 'test', 'over': 'value'}"
						.getBytes());
		BeanClass resourceClass = new DefaultBeanClass(ExtGenericController.class);
		Method method = new Mirror().on(GenericController.class).reflect()
				.method("anotherMethod").withAnyArgs();
		ControllerMethod resource = new DefaultControllerMethod(resourceClass, method);
		when(provider.parameterNamesFor(resource.getMethod())).thenReturn(new String[] { "entity", "param", "over" });

		Object[] deserialized = deserializer.deserialize(stream, resource);

		Dog dog = (Dog) deserialized[0];
		String param = (String) deserialized[1];

		assertThat(dog.name, equalTo("Brutus"));
		assertThat(param, equalTo("test"));
		assertThat(deserialized.length, equalTo(2));
	}

	@Test
	public void shouldDeserializeWithoutGenericType() {
		InputStream stream = new ByteArrayInputStream( "{'param': 'test'}".getBytes());
		BeanClass resourceClass = new DefaultBeanClass(ExtGenericController.class);
		Method method = new Mirror().on(GenericController.class).reflect()
				.method("methodWithoutGenericType").withArgs(String.class);
		ControllerMethod resource = new DefaultControllerMethod(resourceClass, method);
		when(provider.parameterNamesFor(resource.getMethod())).thenReturn(
				new String[] { "param" });

		Object[] deserialized = deserializer.deserialize(stream, resource);

		String param = (String) deserialized[0];

		assertThat(param, equalTo("test"));
	}

	@Test
	public void shouldDeserializeADogWithCalendarWithISO8601() {
		InputStream stream = new ByteArrayInputStream("{'dog':{'name':'Otto','age':2,'birthday':'2013-07-23T17:14:14-03:00'}}"
				.getBytes());

		when(provider.parameterNamesFor(bark.getMethod())).thenReturn(new String[] { "dog" });

		Object[] deserialized = deserializer.deserialize(stream, bark);

		assertThat(deserialized.length, is(1));
		assertThat(deserialized[0], is(instanceOf(Dog.class)));
		Dog dog = (Dog) deserialized[0];
		assertThat(dog.name, is("Otto"));
		assertThat(dog.age, is(2));

		Calendar birthday = new GregorianCalendar(2013, 6, 23, 17, 14, 14);
		birthday.setTimeZone(TimeZone.getTimeZone("GMT-0300"));

		// calendar.equals is too bad :)
		assertThat(dog.birthday.compareTo(birthday), is(0));
	}

}