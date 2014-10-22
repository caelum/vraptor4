package br.com.caelum.vraptor.core;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;

public class ReflectionProviderTest {

	public static class Dog {
		private String name;
		private boolean running;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public boolean isRunning() {
			return running;
		}

		public void setRunning(boolean running) {
			this.running = running;
		}
	}

	public static class DomesticDog extends Dog {
		private String breed;

		public void setBreed(String breed) {
			this.breed = breed;
		}

		public String getBreed() {
			return breed;
		}
	}

	protected ReflectionProvider reflectionProvider;

	@Before
	public void setup() {
		reflectionProvider = new DefaultReflectionProvider();
	}

	@Test
	public void shouldListAllMethodsMethodsFromClass() {
		List<Method> methods = reflectionProvider.getMethodsFor(Dog.class);
		List<String> names = FluentIterable.from(methods).filter(notFromObject()).transform(extractName()).toList();

		assertThat(names, containsInAnyOrder("getName", "isRunning", "setName", "setRunning"));
	}

	@Test
	public void shouldListAllMethodsMethodsFromInheritedClassAndSuperclass() {
		List<Method> methods = reflectionProvider.getMethodsFor(DomesticDog.class);
		List<String> names = FluentIterable.from(methods).filter(notFromObject()).transform(extractName()).toList();

		assertThat(names, containsInAnyOrder("getBreed", "getName", "isRunning", "setBreed", "setName", "setRunning"));
	}

	@Test
	public void shouldReturnMethodFromClass() {
		Method getName = reflectionProvider.getMethod(Dog.class, "getName");
		assertThat(getName.getName(), equalTo("getName"));
		assertThat(getName.getReturnType().getName(), equalTo(String.class.getName()));
		assertThat(getName.getParameterTypes().length, equalTo(0));

		Method setName = reflectionProvider.getMethod(Dog.class, "setName", String.class);
		assertThat(setName.getName(), equalTo("setName"));
		assertThat(setName.getReturnType().getName(), equalTo(void.class.getName()));
		assertThat(setName.getParameterTypes().length, equalTo(1));
	}

	@Test
	public void shouldGetMethodFromInheritedClass() {
		Method getName = reflectionProvider.getMethod(DomesticDog.class, "getName");
		assertThat(getName.getName(), equalTo("getName"));
		assertThat(getName.getReturnType().getName(), equalTo(String.class.getName()));
		assertThat(getName.getParameterTypes().length, equalTo(0));

		Method setBreed = reflectionProvider.getMethod(DomesticDog.class, "setBreed", String.class);
		assertThat(setBreed.getName(), equalTo("setBreed"));
		assertThat(setBreed.getReturnType().getName(), equalTo(void.class.getName()));
		assertThat(setBreed.getParameterTypes().length, equalTo(1));
	}

	@Test
	public void shouldReturnNullWhenMethodNotFound() {
		Method notFound = reflectionProvider.getMethod(Dog.class, "notFound");
		assertThat(notFound, nullValue());
	}

	@Test
	public void shouldInvokeMethodByInstanceFromClass() {
		Dog dog = new Dog();

		Method setName = reflectionProvider.getMethod(Dog.class, "setName", String.class);
		reflectionProvider.invoke(dog, setName, "Otto");
		assertThat(dog.getName(), equalTo("Otto"));
	}

	@Test
	public void shouldInvokeMethodByInstanceFromInheritedClass() {
		DomesticDog dog = new DomesticDog();

		Method setName = reflectionProvider.getMethod(Dog.class, "setName", String.class);
		reflectionProvider.invoke(dog, setName, "Otto");
		assertThat(dog.getName(), equalTo("Otto"));

		Method setBreed = reflectionProvider.getMethod(DomesticDog.class, "setBreed", String.class);
		reflectionProvider.invoke(dog, setBreed, "Schnauzer");
		assertThat(dog.getBreed(), equalTo("Schnauzer"));
	}

	@Test
	public void shouldInvokeMethodByNameFromClass() {
		Dog dog = new Dog();

		reflectionProvider.invoke(dog, "setName", "Otto");
		assertThat(dog.getName(), equalTo("Otto"));
	}

	@Test
	public void shouldInvokeMethodByNameFromInheritedClass() {
		DomesticDog dog = new DomesticDog();

		reflectionProvider.invoke(dog, "setName", "Otto");
		assertThat(dog.getName(), equalTo("Otto"));

		reflectionProvider.invoke(dog, "setBreed", "Schnauzer");
		assertThat(dog.getBreed(), equalTo("Schnauzer"));
	}

	@Test
	public void shouldInvokeGetterFromClass() {
		Dog dog = new Dog();
		dog.setName("Otto");
		dog.setRunning(true);

		String dogName = (String) reflectionProvider.invokeGetter(dog, "name");
		assertThat(dogName, equalTo("Otto"));

		boolean isRunning = (boolean) reflectionProvider.invokeGetter(dog, "running");
		assertThat(isRunning, equalTo(true));
	}

	@Test
	public void shouldInvokeGetterFromInheritedClass() {
		DomesticDog dog = new DomesticDog();
		dog.setName("Otto");
		dog.setBreed("Schnauzer");

		String dogName = (String) reflectionProvider.invokeGetter(dog, "name");
		assertThat(dogName, equalTo("Otto"));

		String dogBreed = (String) reflectionProvider.invokeGetter(dog, "breed");
		assertThat(dogBreed, equalTo("Schnauzer"));
	}

	@Test
	public void shouldGetFieldsFromClass() {
		List<Field> fields = reflectionProvider.getFieldsFor(Dog.class);
		List<String> names = FluentIterable.from(fields).filter(notFromObject()).transform(extractName()).toList();

		assertThat(names, contains("name", "running"));
	}

	@Test
	public void shouldGetFieldsFromInheritedClass() {
		List<Field> fields = reflectionProvider.getFieldsFor(DomesticDog.class);
		List<String> names = FluentIterable.from(fields).filter(notFromObject()).transform(extractName()).toList();

		assertThat(names, contains("breed", "name", "running"));
	}

	@Test
	public void shouldGetFieldByNameFromClass() {
		Field nameField = reflectionProvider.getField(Dog.class, "name");
		assertThat(nameField.getName(), equalTo("name"));
		assertThat(nameField.getType().getName(), equalTo(String.class.getName()));

		Field runningField = reflectionProvider.getField(Dog.class, "running");
		assertThat(runningField.getName(), equalTo("running"));
		assertThat(runningField.getType().getName(), equalTo(boolean.class.getName()));
	}

	@Test
	public void shouldGetFieldByNameFromInheritedClass() {
		Field nameField = reflectionProvider.getField(DomesticDog.class, "name");
		assertThat(nameField.getName(), equalTo("name"));
		assertThat(nameField.getType().getName(), equalTo(String.class.getName()));

		Field breedField = reflectionProvider.getField(DomesticDog.class, "breed");
		assertThat(breedField.getName(), equalTo("breed"));
		assertThat(breedField.getType().getName(), equalTo(String.class.getName()));
	}

	@Test
	public void shouldReturnNullWhenFieldNotFound() {
		Field notFound = reflectionProvider.getField(Dog.class, "notFound");
		assertThat(notFound, nullValue());
	}

	private Function<Member, String> extractName() {
		return new Function<Member, String>() {
			@Override
			public String apply(Member input) {
				return input.getName();
			}
		};
	}

	private Predicate<Member> notFromObject() {
		return new Predicate<Member>() {
			@Override
			public boolean apply(Member input) {
				return !input.getDeclaringClass().equals(Object.class);
			}
		};
	}
}
