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

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

import javax.enterprise.inject.Instance;
import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializer;

import br.com.caelum.vraptor.Consumes;
import br.com.caelum.vraptor.controller.BeanClass;
import br.com.caelum.vraptor.controller.ControllerMethod;
import br.com.caelum.vraptor.controller.DefaultBeanClass;
import br.com.caelum.vraptor.controller.DefaultControllerMethod;
import br.com.caelum.vraptor.core.DefaultReflectionProvider;
import br.com.caelum.vraptor.http.ParameterNameProvider;
import br.com.caelum.vraptor.http.ParanamerNameProvider;
import br.com.caelum.vraptor.ioc.Container;
import br.com.caelum.vraptor.serialization.Deserializee;
import br.com.caelum.vraptor.serialization.Serializee;
import br.com.caelum.vraptor.util.test.MockInstanceImpl;
import br.com.caelum.vraptor.view.GenericController;
import net.vidageek.mirror.dsl.Mirror;

public class GsonDeserializerTest {

	@Rule
	public ExpectedException exception = ExpectedException.none();

	private GsonDeserializerBuilder builder;
	private GsonDeserialization deserializer;
	private ParameterNameProvider provider;
	private HttpServletRequest request;

	private ControllerMethod dogParameter;
	private ControllerMethod dogAndIntegerParameter;
	private ControllerMethod noParameter;
	private ControllerMethod listDog;
	private ControllerMethod integerAndDogParameter;
	private ControllerMethod dateParameter;
	private ControllerMethod dogParameterWithoutRoot;
	private ControllerMethod dogParameterNameEqualsJsonPropertyWithoutRoot;

	private @Mock Container container;

	private @Mock Instance<Deserializee> deserializeeInstance;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		
		TimeZone.setDefault(TimeZone.getTimeZone("GMT-0300"));
		provider = new ParanamerNameProvider();
		request = mock(HttpServletRequest.class);
		List<JsonDeserializer<?>> jsonDeserializers = new ArrayList<>();
		List<JsonSerializer<?>> jsonSerializers = new ArrayList<>();
		jsonDeserializers.add(new CalendarGsonConverter());
		jsonDeserializers.add(new DateGsonConverter());

		builder = new GsonBuilderWrapper(new MockInstanceImpl<>(jsonSerializers), new MockInstanceImpl<>(jsonDeserializers), 
				new Serializee(new DefaultReflectionProvider()), new DefaultReflectionProvider());
		deserializer = new GsonDeserialization(builder, provider, request, container, deserializeeInstance);
		BeanClass controllerClass = new DefaultBeanClass(DogController.class);

		noParameter = new DefaultControllerMethod(controllerClass, DogController.class.getDeclaredMethod("noParameter"));
		dogParameter = new DefaultControllerMethod(controllerClass, DogController.class.getDeclaredMethod("dogParameter", Dog.class));
		dateParameter = new DefaultControllerMethod(controllerClass, DogController.class.getDeclaredMethod("dateParameter", Date.class));
		dogAndIntegerParameter = new DefaultControllerMethod(controllerClass, DogController.class.getDeclaredMethod("dogAndIntegerParameter", Dog.class,
				Integer.class));
		integerAndDogParameter = new DefaultControllerMethod(controllerClass, DogController.class.getDeclaredMethod("integerAndDogParameter",
				Integer.class, Dog.class));
		listDog = new DefaultControllerMethod(controllerClass, DogController.class.getDeclaredMethod("list", List.class));
		dogParameterWithoutRoot = new DefaultControllerMethod(controllerClass, DogController.class.getDeclaredMethod("dogParameterWithoutRoot", Dog.class));
		dogParameterNameEqualsJsonPropertyWithoutRoot = new DefaultControllerMethod(controllerClass, DogController.class.getDeclaredMethod("dogParameterNameEqualsJsonPropertyWithoutRoot", Dog.class));

		when(deserializeeInstance.get()).thenReturn(new Deserializee());
		when(container.instanceFor(WithRoot.class)).thenReturn(new WithRoot());
		when(container.instanceFor(WithoutRoot.class)).thenReturn(new WithoutRoot());
	}

	static class Dog {
		private String name;
		private Integer age;
		private Calendar birthday;
	}
	
	static class DogController {

		public void noParameter() {}

		@Consumes(options=WithRoot.class)
		public void dogParameter(Dog dog) {}

		@Consumes
		public void dogParameterWithoutRoot(Dog dog) {}

		@Consumes(options=WithoutRoot.class)
		public void dogParameterNameEqualsJsonPropertyWithoutRoot(Dog name) {}

		@Consumes
		public void dogAndIntegerParameter(Dog dog, Integer times) {}

		@Consumes
		public void integerAndDogParameter(Integer times, Dog dog) {}

		@Consumes
		public void dateParameter(Date date) {}

		@Consumes
		public void list(List<Dog> dogs) {}
	}

	private class DogDeserializer implements JsonDeserializer<Dog> {

		@Override
		public Dog deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
				throws JsonParseException {
			Dog dog = new Dog();
			dog.name = "Renan";
			dog.age = 25;

			return dog;
		}
	}

	static class DogGenericController extends GenericController<Dog> {

	}

	@Test
	public void shouldDeserializerParseArraysWithoutRoot(){
		InputStream stream = asStream(
			"[" + 
				"{'name':'name1','age':1}," + 
				"{'name':'name2','age':2}," + 
				"{'name':'name3','age':3}" + 
			"]");
 
		Object[] deserialized = deserializer.deserialize(stream, listDog);
		List<Dog>  dogs = (List<Dog>) deserialized[0]; 
		assertThat(dogs.size(), is(3));
		assertThat(dogs.get(0), is(instanceOf(Dog.class)));
		Dog dog = (Dog) dogs.get(0);
		assertThat(dog.name, is("name1"));
		assertThat(dog.age, is(1));
	}

	@Test
	public void shouldDeserializerParseArraysWithRoot(){
		InputStream stream = asStream(
			"{dogs : [" + 
				"{'name':'name1','age':1}," + 
				"{'name':'name2','age':2}," + 
				"{'name':'name3','age':3}" + 
			"]}");
 
		Object[] deserialized = deserializer.deserialize(stream, listDog);
		List<Dog>  dogs = (List<Dog>) deserialized[0]; 
		assertThat(dogs.size(), is(3));
		assertThat(dogs.get(0), is(instanceOf(Dog.class)));
		Dog dog = (Dog) dogs.get(0);
		assertThat(dog.name, is("name1"));
		assertThat(dog.age, is(1));
	}

	@Test
	public void shouldNotAcceptMethodsWithoutArguments() throws Exception {
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("Methods that consumes representations must receive just one argument");

		deserializer.deserialize(emptyStream(), noParameter);
	}

	@Test
	public void shouldBeAbleToDeserializeADog() throws Exception {
		InputStream stream = asStream("{'dog':{'name':'Brutus','age':7}}");

		Object[] deserialized = deserializer.deserialize(stream, dogParameter);

		assertThat(deserialized.length, is(1));
		assertThat(deserialized[0], is(instanceOf(Dog.class)));
		Dog dog = (Dog) deserialized[0];
		assertThat(dog.name, is("Brutus"));
		assertThat(dog.age, is(7));
	}

	@Test
	public void shouldBeAbleToDeserializeADogWithoutRootAndParameterNameEqualsJsonProperty() throws Exception {
		InputStream stream = asStream("{'name':'Brutus','age':7}");

		Object[] deserialized = deserializer.deserialize(stream, dogParameterNameEqualsJsonPropertyWithoutRoot);

		assertThat(deserialized.length, is(1));
		assertThat(deserialized[0], is(instanceOf(Dog.class)));
		Dog dog = (Dog) deserialized[0];
		assertThat(dog.name, is("Brutus"));
		assertThat(dog.age, is(7));
	}

	@Test
	public void shouldBeAbleToDeserializeADogWithoutRoot() throws Exception {
		InputStream stream = asStream("{'name':'Brutus','age':7}");
		
		Object[] deserialized = deserializer.deserialize(stream, dogParameterWithoutRoot);
		
		assertThat(deserialized.length, is(1));
		assertThat(deserialized[0], is(instanceOf(Dog.class)));
		Dog dog = (Dog) deserialized[0];
		assertThat(dog.name, is("Brutus"));
		assertThat(dog.age, is(7));
	}

	@Test
	public void shouldBeAbleToDeserializeADogWithDeserializerAdapter() throws Exception {
		List<JsonDeserializer<?>> deserializers = new ArrayList<>();
		List<JsonSerializer<?>> serializers = new ArrayList<>();
		deserializers.add(new DogDeserializer());

		builder = new GsonBuilderWrapper(new MockInstanceImpl<>(serializers), new MockInstanceImpl<>(deserializers), 
				new Serializee(new DefaultReflectionProvider()), new DefaultReflectionProvider());
		deserializer = new GsonDeserialization(builder, provider, request, container, deserializeeInstance);

		InputStream stream = asStream("{'dog':{'name':'Renan Reis','age':'0'}}");

		Object[] deserialized = deserializer.deserialize(stream, dogParameter);

		assertThat(deserialized.length, is(1));
		assertThat(deserialized[0], is(instanceOf(Dog.class)));
		Dog dog = (Dog) deserialized[0];
		assertThat(dog.name, is("Renan"));
		assertThat(dog.age, is(25));
	}

	@Test
	public void shouldBeAbleToDeserializeADogWhenMethodHasMoreThanOneArgument() throws Exception {
		InputStream stream = asStream("{'dog':{'name':'Brutus','age':7}}");

		Object[] deserialized = deserializer.deserialize(stream, dogAndIntegerParameter);

		assertThat(deserialized.length, is(2));
		assertThat(deserialized[0], is(instanceOf(Dog.class)));
		Dog dog = (Dog) deserialized[0];
		assertThat(dog.name, is("Brutus"));
		assertThat(dog.age, is(7));
	}

	@Test
	public void shouldBeAbleToDeserializeADogWhenMethodHasMoreThanOneArgumentAndJsonIsTheLastOne() throws Exception {
		InputStream stream = asStream("{'dog':{'name':'Brutus','age':7}}");

		Object[] deserialized = deserializer.deserialize(stream, integerAndDogParameter);

		assertThat(deserialized.length, is(2));
		assertThat(deserialized[1], is(instanceOf(Dog.class)));
		Dog dog = (Dog) deserialized[1];
		assertThat(dog.name, is("Brutus"));
		assertThat(dog.age, is(7));
	}

	@Test
	public void shouldBeAbleToDeserializeADogNamedDifferently() throws Exception {
		InputStream stream = asStream("{'dog':{'name':'Brutus','age':7}}");

		Object[] deserialized = deserializer.deserialize(stream, dogParameter);

		assertThat(deserialized.length, is(1));
		assertThat(deserialized[0], is(instanceOf(Dog.class)));
		Dog dog = (Dog) deserialized[0];
		assertThat(dog.name, is("Brutus"));
		assertThat(dog.age, is(7));
	}

	@Test
	public void shouldHonorRequestHeaderAcceptCharset() throws Exception {
		InputStream stream = asStream("{'dog':{'name':'Ã§'}}", StandardCharsets.ISO_8859_1);
		
		when(request.getHeader("Accept-Charset")).thenReturn("UTF-8,*;q=0.5");
		
		Object[] deserialized = deserializer.deserialize(stream, dogParameter);

		assertThat(deserialized.length, is(1));
		assertThat(deserialized[0], is(instanceOf(Dog.class)));

		Dog dog = (Dog) deserialized[0];

		assertThat(dog.name, is("ç"));
	}

	@Test
	public void whenNoCharsetHeaderIsFoundThanAssumeItIsUTF8() throws Exception {
		InputStream stream = asStream("{'dog':{'name':'Ã§'}}",  StandardCharsets.ISO_8859_1);

		when(request.getHeader("Accept-Charset")).thenReturn(null);
		
		Object[] deserialized = deserializer.deserialize(stream, dogParameter);

		assertThat(deserialized.length, is(1));
		assertThat(deserialized[0], is(instanceOf(Dog.class)));

		Dog dog = (Dog) deserialized[0];

		assertThat(dog.name, is("ç"));
	}

	@Test
	public void shouldByPassDeserializationWhenHasNoContent() {
		Object[] deserialized = deserializer.deserialize(emptyStream(), dogParameter);

		assertThat(deserialized.length, is(1));
		assertThat(deserialized[0], is(nullValue()));
	}

	@Test
	public void shouldBeAbleToDeserializeADogWhenMethodHasMoreThanOneArgumentAndHasNotRoot() {
		InputStream stream = asStream("{'name':'Brutus','age':7}");

		Object[] deserialized = deserializer.deserialize(stream, dogAndIntegerParameter);

		assertThat(deserialized[0], is(instanceOf(Dog.class)));
		Dog dog = (Dog) deserialized[0];
		assertThat(dog.name, is("Brutus"));
		assertThat(dog.age, is(7));
	}

	@Test
	public void shouldDeserializeFromGenericTypeOneParam() {
		InputStream stream = asStream("{'entity':{'name':'Brutus','age':7,'birthday':'2013-07-23T17:14:14-03:00'}}");
		BeanClass resourceClass = new DefaultBeanClass(DogGenericController.class);
		Method method = new Mirror().on(DogGenericController.class).reflect().method("method").withAnyArgs();
		ControllerMethod resource = new DefaultControllerMethod(resourceClass, method);
		
		Object[] deserialized = deserializer.deserialize(stream, resource);

		Dog dog = (Dog) deserialized[0];

		assertThat(dog.name, equalTo("Brutus"));
	}

	@Test
	public void shouldDeserializeFromGenericTypeWithoutRoot() {
		InputStream stream = asStream("{'name':'Brutus','age':7,'birthday':'2013-07-23T17:14:14-03:00'}");
		BeanClass resourceClass = new DefaultBeanClass(DogGenericController.class);
		Method method = new Mirror().on(DogGenericController.class).reflect().method("method").withAnyArgs();
		ControllerMethod resource = new DefaultControllerMethod(resourceClass, method);

		Object[] deserialized = deserializer.deserialize(stream, resource);

		Dog dog = (Dog) deserialized[0];

		assertThat(dog.name, equalTo("Brutus"));
		assertThat(dog.age, equalTo(7));
	}

	@Test
	public void shouldDeserializeFromGenericTypeTwoParams() {
		InputStream stream = asStream("{'entity':{'name':'Brutus','age':7,'birthday':'2013-07-23T17:14:14-03:00'}, 'param': 'test', 'over': 'value'}");
		BeanClass resourceClass = new DefaultBeanClass(DogGenericController.class);
		Method method = new Mirror().on(DogGenericController.class).reflect().method("anotherMethod").withAnyArgs();
		ControllerMethod resource = new DefaultControllerMethod(resourceClass, method);
		
		Object[] deserialized = deserializer.deserialize(stream, resource);

		Dog dog = (Dog) deserialized[0];
		String param = (String) deserialized[1];

		assertThat(dog.name, equalTo("Brutus"));
		assertThat(param, equalTo("test"));
		assertThat(deserialized.length, equalTo(2));
	}

	@Test
	public void shouldDeserializeADogWithCalendarWithISO8601() {
		InputStream stream = asStream("{'dog':{'name':'Otto','age':2,'birthday':'2013-07-23T17:14:14-03:00'}}");

		Object[] deserialized = deserializer.deserialize(stream, dogParameter);

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

	@Test
	public void shouldDeserializeADateWithISO8601() {
		InputStream stream = asStream("{\"date\":\"1988-02-25T02:30:15 -0300\"}");
		
		Object[] deserialized = deserializer.deserialize(stream, dateParameter);
		assertThat(deserialized.length, is(1));
		assertThat(deserialized[0], is(instanceOf(Date.class)));
		Date deserializedDate = (Date) deserialized[0];
		Date date = new GregorianCalendar(1988, 1, 25, 2, 30, 15).getTime();
		assertEquals(date, deserializedDate);
	}
	
	private InputStream asStream(String str, Charset charset) {
		return new ByteArrayInputStream(str.getBytes(charset));
	}

	private InputStream asStream(String str) {
		return asStream(str, Charset.defaultCharset());
	}

	private InputStream emptyStream() {
		return new ByteArrayInputStream(new byte[0]);
	}
}
