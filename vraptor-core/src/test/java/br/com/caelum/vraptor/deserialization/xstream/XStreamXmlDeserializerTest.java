package br.com.caelum.vraptor.deserialization.xstream;

import static br.com.caelum.vraptor.serialization.xstream.XStreamBuilderFactory.cleanInstance;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.AccessibleObject;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.cache.DefaultCacheStore;
import br.com.caelum.vraptor.controller.BeanClass;
import br.com.caelum.vraptor.controller.ControllerMethod;
import br.com.caelum.vraptor.controller.DefaultBeanClass;
import br.com.caelum.vraptor.controller.DefaultControllerMethod;
import br.com.caelum.vraptor.http.Parameter;
import br.com.caelum.vraptor.http.ParameterNameProvider;
import br.com.caelum.vraptor.http.ParanamerNameProvider;
import br.com.caelum.vraptor.serialization.xstream.CalendarConverter;

import com.thoughtworks.xstream.annotations.XStreamAlias;

public class XStreamXmlDeserializerTest {

	private XStreamXMLDeserializer deserializer;
	private ControllerMethod bark;
	private ParameterNameProvider provider;
	private ControllerMethod jump;
	private ControllerMethod woof;
	private ControllerMethod dropDead;
	private ControllerMethod annotated;

	@Before
	public void setUp() throws Exception {
		provider = new ParanamerNameProvider(new DefaultCacheStore<AccessibleObject, Parameter[]>());

		deserializer = new XStreamXMLDeserializer(provider, cleanInstance(new CalendarConverter()));
		BeanClass controllerClass = new DefaultBeanClass(DogController.class);

		woof = new DefaultControllerMethod(controllerClass, DogController.class.getDeclaredMethod("woof"));
		bark = new DefaultControllerMethod(controllerClass, DogController.class.getDeclaredMethod("bark", Dog.class));
		jump = new DefaultControllerMethod(controllerClass, DogController.class.getDeclaredMethod("jump", Dog.class, Integer.class));
		dropDead = new DefaultControllerMethod(controllerClass, DogController.class.getDeclaredMethod("dropDead", Integer.class, Dog.class));
		annotated = new DefaultControllerMethod(controllerClass, DogController.class.getDeclaredMethod("annotated", DogWithAnnotations.class));
	}

	@XStreamAlias("dogAnnotated")
	static class DogWithAnnotations {

		@XStreamAlias("nameAnnotated")
		private String name;

		@XStreamAlias("ageAnnotated")
		private Integer age;
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
		public void annotated(DogWithAnnotations dog){
		}

	}

	@Test(expected=IllegalArgumentException.class)
	public void shouldNotAcceptMethodsWithoutArguments() throws Exception {
		deserializer.deserialize(new ByteArrayInputStream(new byte[0]), woof);
	}
	@Test
	public void shouldBeAbleToDeserializeADog() throws Exception {
		InputStream stream = new ByteArrayInputStream("<dog><name>Brutus</name><age>7</age></dog>".getBytes());


		Object[] deserialized = deserializer.deserialize(stream, bark);

		assertThat(deserialized.length, is(1));
		assertThat(deserialized[0], is(instanceOf(Dog.class)));
		Dog dog = (Dog) deserialized[0];
		assertThat(dog.name, is("Brutus"));
		assertThat(dog.age, is(7));
	}

	@Test
	public void shouldBeAbleToDeserializeADogAsISO8601() throws Exception {
		InputStream stream = new ByteArrayInputStream("<dog><name>Otto</name><age>2</age><birthday>2013-07-23T17:14:14-03:00</birthday></dog>"
				.getBytes());

		Object[] deserialized = deserializer.deserialize(stream, bark);

		Calendar birthday = new GregorianCalendar(2013, 6, 23, 17, 14, 14);
		birthday.setTimeZone(TimeZone.getTimeZone("GMT-0300"));

		assertThat(deserialized.length, is(1));
		assertThat(deserialized[0], is(instanceOf(Dog.class)));

		Dog dog = (Dog) deserialized[0];
		assertThat(dog.name, is("Otto"));
		assertThat(dog.age, is(2));

		// calendar.equals is too bad :)
		assertThat(dog.birthday.compareTo(birthday), is(0));
	}

	@Test
	public void shouldBeAbleToDeserializeADogWhenMethodHasMoreThanOneArgument() throws Exception {
		InputStream stream = new ByteArrayInputStream("<dog><name>Brutus</name><age>7</age></dog>".getBytes());

		Object[] deserialized = deserializer.deserialize(stream, jump);

		assertThat(deserialized.length, is(2));
		assertThat(deserialized[0], is(instanceOf(Dog.class)));
		Dog dog = (Dog) deserialized[0];
		assertThat(dog.name, is("Brutus"));
		assertThat(dog.age, is(7));
	}
	@Test
	public void shouldBeAbleToDeserializeADogWhenMethodHasMoreThanOneArgumentAndTheXmlIsTheLastOne() throws Exception {
		InputStream stream = new ByteArrayInputStream("<dog><name>Brutus</name><age>7</age></dog>".getBytes());

		Object[] deserialized = deserializer.deserialize(stream, dropDead);

		assertThat(deserialized.length, is(2));
		assertThat(deserialized[1], is(instanceOf(Dog.class)));
		Dog dog = (Dog) deserialized[1];
		assertThat(dog.name, is("Brutus"));
		assertThat(dog.age, is(7));
	}

	@Test
	public void shouldBeAbleToDeserializeADogNamedDifferently() throws Exception {
		InputStream stream = new ByteArrayInputStream("<dog><name>Brutus</name><age>7</age></dog>".getBytes());

		Object[] deserialized = deserializer.deserialize(stream, bark);

		assertThat(deserialized.length, is(1));
		assertThat(deserialized[0], is(instanceOf(Dog.class)));
		Dog dog = (Dog) deserialized[0];
		assertThat(dog.name, is("Brutus"));
		assertThat(dog.age, is(7));
	}

	@Test
	public void shouldBeAbleToDeserializeADogWhenAliasConfiguredByAnnotations() {

		InputStream stream = new ByteArrayInputStream("<dogAnnotated><nameAnnotated>Lubi</nameAnnotated><ageAnnotated>8</ageAnnotated></dogAnnotated>".getBytes());

		Object[] deserialized = deserializer.deserialize(stream, annotated);

		assertThat(deserialized.length, is(1));
		assertThat(deserialized[0], is(instanceOf(DogWithAnnotations.class)));

		DogWithAnnotations dog = (DogWithAnnotations) deserialized[0];
		assertThat(dog.name, is("Lubi"));
		assertThat(dog.age, is(8));
	}

}
