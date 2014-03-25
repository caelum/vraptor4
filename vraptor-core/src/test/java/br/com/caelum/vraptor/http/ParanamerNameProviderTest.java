/***
 * Copyright (c) 2009 Caelum - www.caelum.com.br/opensource
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * 
 * 	http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License. 
 */
package br.com.caelum.vraptor.http;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Named;

import org.junit.Test;

public class ParanamerNameProviderTest {

	private ParanamerNameProvider provider = new ParanamerNameProvider();

	private List<String> toNames(Parameter[] parameters) {
		List<String> out = new ArrayList<>();
		for (Parameter p : parameters)
			out.add(p.getName());
		return out;
	}

	@Test
	public void shouldNameObjectTypeAsItsSimpleName() throws SecurityException, NoSuchMethodException {
		Parameter[] namesFor = provider.parametersFor(Horse.class.getMethod("runThrough", Field.class));
		assertThat(toNames(namesFor), contains("f"));
	}

	@Test
	public void shouldNamePrimitiveTypeAsItsSimpleName() throws SecurityException, NoSuchMethodException {
		Parameter[] namesFor = provider.parametersFor(Horse.class.getMethod("rest", int.class));
		assertThat(toNames(namesFor), contains("hours"));
	}

	@Test
	public void shouldNameArrayAsItsSimpleTypeName() throws SecurityException, NoSuchMethodException {
		Parameter[] namesFor = provider.parametersFor(Horse.class.getMethod("setLeg", int[].class));
		assertThat(toNames(namesFor), contains("length"));
	}

	@Test
	public void shouldNameGenericCollectionUsingOf() throws SecurityException, NoSuchMethodException {
		Parameter[] namesFor = provider.parametersFor(Cat.class.getDeclaredMethod("fightWith", List.class));
		assertThat(toNames(namesFor), contains("cats"));
	}
	
	@Test
	public void shouldIgnoreChangesToTheReturnedArrayInSubsequentCalls() throws Exception {
		Parameter[] firstCall = provider.parametersFor(Horse.class.getMethod("setLeg", int[].class));
		firstCall[0] = null;

		Parameter[] secondCall = provider.parametersFor(Horse.class.getMethod("setLeg", int[].class));
		assertThat(secondCall[0], notNullValue());
	}
	
	@Test
	public void shouldNameFieldsAnnotatedWithNamed() throws SecurityException, NoSuchMethodException  {
		Parameter[] namesFor = provider.parametersFor(Horse.class.getMethod("runThroughWithAnnotation", Field.class));
		assertThat(toNames(namesFor), contains("one"));
	}
	
	@Test
	public void shouldNotNameFieldsByTheFieldNameWhenUsingAnnotation() throws SecurityException, NoSuchMethodException  {
		Parameter[] namesFor = provider.parametersFor(Horse.class.getMethod("runThroughWithAnnotation", Field.class));
		assertThat(toNames(namesFor), not(contains("field")));
	}

	@Test
	public void shouldNameMethodsFieldsWhenAnnotatedOrNot() throws SecurityException, NoSuchMethodException  {
		Parameter[] namesFor = provider.parametersFor(Horse.class.getMethod("runThroughWithAnnotation2", Field.class, Field.class));
		assertThat(toNames(namesFor), contains("one", "two"));
	}
	
	@Test
	public void shouldNameMethodsFieldsWhenAnnotatedOrNot2() throws SecurityException, NoSuchMethodException  {
		Parameter[] namesFor = provider.parametersFor(Horse.class.getMethod("runThroughWithAnnotation3", Field.class, Field.class));
		assertThat(toNames(namesFor), contains("one", "size"));
	}

	static class Field {
	}

	public static class Horse {
		public void runThrough(Field f) {
		}

		public void rest(int hours) {
		}

		public void setLeg(int[] length) {
		}
		
		public void runThroughWithAnnotation(@Named(value="one") Field field) {
			
		}
		
		public void runThroughWithAnnotation2(@Named(value="one") Field f, Field two) {

		}

		public void runThroughWithAnnotation3(Field one, @Named(value="size") Field two) {
			
		}
	}

	public static class Cat {
		void fightWith(List<String> cats) {
		}
	}
}
