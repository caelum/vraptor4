package br.com.caelum.vraptor4.deserialization.gson;

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor4.controller.BeanClass;
import br.com.caelum.vraptor4.controller.ControllerMethod;
import br.com.caelum.vraptor4.controller.DefaultBeanClass;
import br.com.caelum.vraptor4.controller.DefaultControllerMethod;
import br.com.caelum.vraptor4.core.Localization;
import br.com.caelum.vraptor4.http.ParameterNameProvider;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

public class GsonDeserializerTest {

	private GsonDeserialization deserializer;
	private ControllerMethod bark;
	private ParameterNameProvider provider;
	private Localization localization;
	private ControllerMethod jump;
	private ControllerMethod woof;
	private ControllerMethod dropDead;
	private HttpServletRequest request;

	@Before
	public void setUp() throws Exception {
		provider = mock(ParameterNameProvider.class);
		localization = mock(Localization.class);
		request = mock(HttpServletRequest.class);

		when(localization.getLocale()).thenReturn(new Locale("pt", "BR"));

		deserializer = new GsonDeserialization(provider, Collections.<JsonDeserializer> emptyList(), request);
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
		List<JsonDeserializer> deserializers = new ArrayList<>();
		deserializers.add(new DogDeserializer());

		deserializer = new GsonDeserialization(provider, deserializers, request);

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
}