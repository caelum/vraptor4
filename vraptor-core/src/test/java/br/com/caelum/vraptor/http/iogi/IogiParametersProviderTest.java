/***
 *
 * Copyright (c) 2009 Caelum - www.caelum.com.br/opensource All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer. 2. Redistributions in
 * binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution. 3. Neither the name of the
 * copyright holders nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written
 * permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package br.com.caelum.vraptor.http.iogi;

import static br.com.caelum.vraptor.VRaptorMatchers.hasMessage;
import static br.com.caelum.vraptor.controller.DefaultControllerMethod.instanceFor;
import static java.util.Collections.singletonMap;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.arrayContaining;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.emptyArray;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertArrayEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.com.caelum.iogi.parameters.Parameter;
import br.com.caelum.iogi.parameters.Parameters;
import br.com.caelum.iogi.reflection.Target;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.controller.ControllerMethod;
import br.com.caelum.vraptor.controller.DefaultControllerMethod;
import br.com.caelum.vraptor.converter.Converter;
import br.com.caelum.vraptor.converter.LongConverter;
import br.com.caelum.vraptor.converter.PrimitiveLongConverter;
import br.com.caelum.vraptor.converter.StringConverter;
import br.com.caelum.vraptor.core.Converters;
import br.com.caelum.vraptor.http.InvalidParameterException;
import br.com.caelum.vraptor.http.ParameterNameProvider;
import br.com.caelum.vraptor.http.ParametersProvider;
import br.com.caelum.vraptor.http.ParanamerNameProvider;
import br.com.caelum.vraptor.ioc.Container;
import br.com.caelum.vraptor.validator.DefaultValidationException;
import br.com.caelum.vraptor.validator.Message;

import com.google.common.collect.ImmutableMap;

public class IogiParametersProviderTest {

	@Rule
	public ExpectedException exception = ExpectedException.none();

	private @Mock Converters converters;
	private ParameterNameProvider nameProvider;
	private @Mock HttpServletRequest request;
	private @Mock Container container;

	private ArrayList<Message> errors;
	private ParametersProvider provider;
	private ControllerMethod buyA;
	private ControllerMethod kick;
	private ControllerMethod error;
	private ControllerMethod array;
	private ControllerMethod simple;

	private ControllerMethod list;
	private ControllerMethod listOfObject;
	private ControllerMethod abc;
	private ControllerMethod string;
	private ControllerMethod generic;
	private ControllerMethod primitive;
	private ControllerMethod stringArray;
	private ControllerMethod dependency;
	private ControllerMethod doNothing;

	@Before
	public void setup() throws Exception {
		MockitoAnnotations.initMocks(this);
		nameProvider = new ParanamerNameProvider();
		provider = getProvider();
		errors = new ArrayList<>();
		
		when(converters.existsFor(Long.class)).thenReturn(true);
		when(converters.existsFor(long.class)).thenReturn(true);
		when(converters.existsFor(String.class)).thenReturn(true);
		when(converters.to(Long.class)).thenReturn(new LongConverter());
		when(converters.to(long.class)).thenReturn(new PrimitiveLongConverter());
		when(converters.to(String.class)).thenReturn(new StringConverter());

		buyA 		= method("buyA", House.class);
		kick 		= method("kick", AngryCat.class);
		error 		= method("error", WrongCat.class);
		array 		= method("array", Long[].class);
		list 		= method("list", List.class);
		listOfObject= method("listOfObject", List.class);
		abc 		= method("abc", ABC.class);
		simple 		= method("simple", Long.class);
		string 		= method("string", String.class);
		stringArray = method("stringArray", String[].class);
		dependency 	= method("dependency", Result.class);
		primitive 	= method("primitive", long.class);
		doNothing 	= method("doNothing");
		generic 	= DefaultControllerMethod.instanceFor(Specific.class, Generic.class.getDeclaredMethod("generic", Object.class));
	}

	@Test
	public void isCapableOfDealingWithStrings() throws Exception {
		requestParameterIs(string, "abc", "eureka");

		String abc = getFirstParameterFor(string);

		assertThat(abc, is("eureka"));
	}

	@Test
	public void isCapableOfDealingWithStringArrays() throws Exception {
		requestParameterIs(stringArray, "abc", "eureka");

		String[] abc = getFirstParameterFor(stringArray);

		assertThat(abc, is(new String[] {"eureka"}));
	}

	@Test
	public void isCapableOfDealingWithIndexedStringArrays() throws Exception {
		requestParameterIs(stringArray, "abc[0]", "eureka");

		String[] abc = getFirstParameterFor(stringArray);

		assertThat(abc, is(new String[] {"eureka"}));
	}

	@Test
	public void isCapableOfDealingWithGenerics() throws Exception {
		requestParameterIs(generic, "t.x", "123");
		
		ABC abc = getFirstParameterFor(generic);

		assertThat(abc.x, is(123l));
	}

	@Test
	public void isCapableOfDealingWithIndexedLists() throws Exception {
		requestParameterIs(list, "abc[2]", "1");

		List<Long> abc = getFirstParameterFor(list);

		assertThat(abc, hasSize(1));
		assertThat(abc, hasItem(1l));
	}

	@Test
	public void isCapableOfDealingWithIndexedListsOfObjects() throws Exception {
		requestParameterIs(listOfObject, "abc[2].x", "1");

		List<ABC> abc = getFirstParameterFor(listOfObject);

		assertThat(abc, hasSize(1));
		assertThat(abc.get(0).x, is(1l));
	}

	@Test
	public void isCapableOfDealingWithLists() throws Exception {
		requestParameterIs(list, "abc", "1");

		List<Long> abc = getFirstParameterFor(list);

		assertThat(abc, hasSize(1));
		assertThat(abc, hasItem(1l));
	}

	@Test
	public void isCapableOfDealingIndexedArraysWithOneElement() throws Exception {
		requestParameterIs(array, "abc[2]", "1");

		Long[] abc = getFirstParameterFor(array);

		assertThat(abc, is(arrayContaining(1l)));
	}

	@Test
	public void isCapableOfDealingArraysWithOneElement() throws Exception {
		requestParameterIs(array, "abc", "1");

		Long[] abc = getFirstParameterFor(array);

		assertThat(abc, is(arrayContaining(1l)));
	}

	@Test
	public void isCapableOfDealingArraysWithSeveralElements() throws Exception {
		requestParameterIs(array, "abc", "1", "2", "3");

		Long[] abc = getFirstParameterFor(array);

		assertThat(abc, is(arrayContaining(1l, 2l, 3l)));
	}

	@Test
	public void isCapableOfDealingWithEmptyParameterForInternalWrapperValue() throws Exception {
		requestParameterIs(buyA, "house.cat.id", "guilherme");

		House house = getFirstParameterFor(buyA);

		assertThat(house.cat.id, is(equalTo("guilherme")));
	}

	@Test
	public void removeFromTheCollectionIfAnElementIsCreatedWithinACollectionButNoFieldIsSet() throws Exception {
		requestParameterIs(buyA, "house.extraCats[1].id", "guilherme");

		House house = getFirstParameterFor(buyA);

		assertThat(house.extraCats, hasSize(1));
		assertThat(house.extraCats.get(0).id, is(equalTo("guilherme")));
	}

	@Test
	public void removeFromTheCollectionIfAnElementIsCreatedWithinAnArrayButNoFieldIsSet() throws Exception {
		requestParameterIs(buyA, "house.ids[1]", "3");

		House house = getFirstParameterFor(buyA);

		assertThat(house.ids.length, is(equalTo(1)));
		assertThat(house.ids[0], is(equalTo(3L)));
	}

	@Test
	public void removeFromTheCollectionIfAnElementIsCreatedWithinACollectionButNoFieldIsSetAppartFromTheValueItselfNotAChild()
			throws Exception {
		requestParameterIs(buyA, "house.owners[1]", "guilherme");

		House house = getFirstParameterFor(buyA);

		assertThat(house.owners, hasSize(1));
		assertThat(house.owners.get(0), is(equalTo("guilherme")));
	}

	@Test
	public void addsValidationMessageWhenSetterFailsWithAValidationException() throws Exception {
		requestParameterIs(kick, "angryCat.id", "guilherme");

		getFirstParameterFor(kick);

		assertThat(errors.size(), is(greaterThan(0)));
	}

	@Test
	public void throwsExceptionWhenSetterFailsWithOtherException() throws Exception {
		exception.expect(InvalidParameterException.class);
		exception.expectMessage(containsString("Exception when trying to instantiate"));

		requestParameterIs(error, "wrongCat.id", "guilherme");

		getFirstParameterFor(error);
	}

	@Test
	public void returnsASimpleValue() throws Exception {
		requestParameterIs(simple, "xyz", "42");

		Long xyz = getFirstParameterFor(simple);
		assertThat(xyz, is(42l));
	}

	@Test
	public void addsValidationErrorsOnConvertionErrors() throws Exception {
		requestParameterIs(simple, "xyz", "4s2");

		getFirstParameterFor(simple);
		assertThat(errors, hasSize(1));
	}

	@Test
	public void returnsNullWhenThereAreNoParameters() throws Exception {
		thereAreNoParameters();

		Long xyz = getFirstParameterFor(simple);
		assertThat(xyz, is(nullValue()));
	}

	@Test
	public void returnsDependenciesIfContainerCanProvide() throws Exception {
		thereAreNoParameters();
		Result result = mock(Result.class);

		when(container.canProvide(Result.class)).thenReturn(true);
		when(container.instanceFor(Result.class)).thenReturn(result);
		
		Result returned = getFirstParameterFor(dependency);
		assertThat(returned, is(result));
	}

	@Test
	public void returnsDependenciesIfRequestCanProvide() throws Exception {
		thereAreNoParameters();
		Result result = mock(Result.class);

		when(request.getAttribute("result")).thenReturn(result);

		Result returned = getFirstParameterFor(dependency);
		assertThat(returned, is(result));
	}

	@Test
	public void ignoresPopulationIfIfRequestCanProvide() throws Exception {
		requestParameterIs(abc, "abc.x", "1");
		ABC expected = new ABC();
		expected.setX(2l);

		when(request.getAttribute("abc")).thenReturn(expected);

		ABC returned = getFirstParameterFor(abc);
		assertThat(returned.getX(), is(2l));
	}

	@Test
	public void doesntReturnDependenciesIfItIsNotAnInterface() throws Exception {
		thereAreNoParameters();
		ABC result = mock(ABC.class);
		when(container.canProvide(ABC.class)).thenReturn(true);
		when(container.instanceFor(ABC.class)).thenReturn(result);

		ABC returned = getFirstParameterFor(abc);
		assertThat(returned, is(not(result)));
	}

	@Test
	public void returnsZeroForAPrimitiveWhenThereAreNoParameters() throws Exception {
		thereAreNoParameters();
		
		Long xyz = getFirstParameterFor(primitive);
		assertThat(xyz, is(0l));
	}

	@Test
	public void continuesToFillObjectIfItIsConvertable() throws Exception {
		ImmutableMap<String, String[]> params = ImmutableMap.of("abc", new String[] {""}, "abc.x", new String[] {"3"});
		when(request.getParameterMap()).thenReturn(params);
		
		when(converters.existsFor(ABC.class)).thenReturn(true);
		when(converters.to(ABC.class)).thenReturn(new Converter<ABC>() {
			@Override
			public ABC convert(String value, Class<? extends ABC> type) {
				return new ABC();
			}
		});

		ABC returned = getFirstParameterFor(abc);
		assertThat(returned.x, is(3l));
	}

	@Test
	public void continuesToFillObjectWithListIfItIsConvertable() throws Exception {
		ImmutableMap<String, String[]> params = ImmutableMap.of("abc", new String[] {""}, "abc.person[0].name", new String[] {"bird"}, "abc.person[1].name", new String[] {"bird 2"});
		when(request.getParameterMap()).thenReturn(params);
		
		when(converters.existsFor(ABC.class)).thenReturn(true);
		when(converters.to(ABC.class)).thenReturn(new Converter<ABC>() {
			@Override
			public ABC convert(String value, Class<? extends ABC> type) {
				return new ABC();
			}
		});
		
		ABC returned = getFirstParameterFor(abc);
		assertThat(returned.getPerson().get(0).getName(), is("bird"));
		assertThat(returned.getPerson().get(1).getName(), is("bird 2"));
	}

	@Test
	public void continuesToFillObjectWithSetIfItIsConvertable() throws Exception {
		ImmutableMap<String, String[]> params = ImmutableMap.of("abc", new String[] {""}, "abc.addresses[0].street", new String[] {"Some Street"}, "abc.addresses[1].street", new String[] {"Some Street 2"});
		when(request.getParameterMap()).thenReturn(params);
		
		when(converters.existsFor(ABC.class)).thenReturn(true);
		when(converters.to(ABC.class)).thenReturn(new Converter<ABC>() {
			@Override
			public ABC convert(String value, Class<? extends ABC> type) {
				return new ABC();
			}
		});
		
		ABC returned = getFirstParameterFor(abc);
		
		Address[] address = (Address[]) returned.getAddresses().toArray(new Address[2]);
		List<String> streets = Arrays.asList(address[0].getStreet(), address[1].getStreet());
		
		assertThat(streets, containsInAnyOrder("Some Street", "Some Street 2"));
	}

	@Test
	public void returnsAnEmptyObjectArrayForZeroArityMethods() throws Exception {
		thereAreNoParameters();

		Object[] params = provider.getParametersFor(doNothing, errors);

		assertThat(params, emptyArray());
	}

	@Test
	public void returnsNullWhenInstantiatingAListForWhichThereAreNoParameters() throws Exception {
		thereAreNoParameters();

		Object[] params = provider.getParametersFor(list, errors);

		assertArrayEquals(new Object[] {null}, params);
	}

	@Test
	public void shouldInstantiateTheObjectEvenWhenThereAreNoParameters() throws Exception {
		thereAreNoParameters();
		Method setCat = House.class.getMethod("setCat", Cat.class);
		ControllerMethod method = instanceFor(House.class, setCat);
		Object[] params = provider.getParametersFor(method, errors);
		assertThat(params[0], is(notNullValue()));
		assertThat(params[0], instanceOf(Cat.class));
	}

	@Test
	public void shouldnotInstantiateObjectWhenThereAreNoParameters() throws Exception {
		VRaptorInstantiator instantiator = new NullVRaptorInstantiator(converters, 
				new VRaptorDependencyProvider(container), 
				new VRaptorParameterNamesProvider(nameProvider), request);
		
		instantiator.createInstantiator();
		IogiParametersProvider provider = new IogiParametersProvider(nameProvider, request, instantiator);
		
		thereAreNoParameters();
		Method setCat = House.class.getMethod("setCat", Cat.class);
		ControllerMethod method = instanceFor(House.class, setCat);
		Object[] params = provider.getParametersFor(method, errors);
		assertThat(params[0], is(nullValue()));
	}

	static class NullVRaptorInstantiator extends VRaptorInstantiator {
		
		public NullVRaptorInstantiator(Converters converters,
				VRaptorDependencyProvider provider,
				VRaptorParameterNamesProvider nameProvider,
				HttpServletRequest request) {
			super(converters, provider, nameProvider, request);
		}
		
		@Override
		protected boolean useNullForMissingParameters() {
			return true;
		}
	}
	
	@Test
	public void returnsNullWhenInstantiatingAStringForWhichThereAreNoParameters() throws Exception {
		thereAreNoParameters();
		final ControllerMethod method = string;

		Object[] params = provider.getParametersFor(method, errors);

		assertArrayEquals(new Object[] {null}, params);
	}

	@Test
	public void canInjectADependencyProvidedByVraptor() throws Exception {
		thereAreNoParameters();

		ControllerMethod controllerMethod = DefaultControllerMethod.instanceFor(OtherResource.class, OtherResource.class.getDeclaredMethod("logic", NeedsMyResource.class));
		final MyResource providedInstance = new MyResource();

		when(container.canProvide(MyResource.class)).thenReturn(true);
		when(container.instanceFor(MyResource.class)).thenReturn(providedInstance);
		
		Object[] params = provider.getParametersFor(controllerMethod, errors);
		assertThat(((NeedsMyResource)params[0]).getMyResource(), is(sameInstance(providedInstance)));
	}

	//---------- The Following tests mock iogi to unit test the ParametersProvider impl.
	@Test
	public void willCreateAnIogiParameterForEachRequestParameterValue() throws Exception {
		ControllerMethod anyMethod = buyA;
		requestParameterIs(anyMethod, "name", "a", "b");

		final InstantiatorWithErrors mockInstantiator = mock(InstantiatorWithErrors.class);
		final Parameters expectedParamters = new Parameters(new Parameter("name", "a"), new Parameter("name", "b"));

		IogiParametersProvider iogiProvider = new IogiParametersProvider(nameProvider, request, mockInstantiator);

		iogiProvider.getParametersFor(anyMethod, errors);

		verify(mockInstantiator).instantiate(any(Target.class), eq(expectedParamters), eq(errors));
	}

	@Test
	public void willCreateATargerForEachFormalParameterDeclaredByTheMethod() throws Exception {
		final ControllerMethod buyAHouse = buyA;
		requestParameterIs(buyAHouse, "house", "");

		final InstantiatorWithErrors mockInstantiator = mock(InstantiatorWithErrors.class);
		IogiParametersProvider iogiProvider = new IogiParametersProvider(nameProvider, request, mockInstantiator);
		final Target<House> expectedTarget = Target.create(House.class, "house");

		iogiProvider.getParametersFor(buyAHouse, errors);

		verify(mockInstantiator).instantiate(eq(expectedTarget), any(Parameters.class), eq(errors));
	}

	@Test
	public void willAddValidationMessagesForConversionErrors() throws Exception {
		ControllerMethod setId = simple;
		requestParameterIs(setId, "xyz", "asdf");
		
		getFirstParameterFor(setId);

		assertThat(errors, hasSize(1));
		assertThat(errors.get(0), hasMessage("asdf is not a valid number."));
		assertThat(errors.get(0).getCategory(), is("xyz"));
	}

	@Test
	public void inCaseOfConversionErrorsOnlyNullifyTheProblematicParameter() throws Exception {
		ControllerMethod setId = DefaultControllerMethod.instanceFor(House.class, House.class.getMethod("setCat", Cat.class));
		requestParameterIs(setId, "cat.lols", "sad kitten");

		Cat cat = getFirstParameterFor(setId);
		assertThat(cat, is(notNullValue()));
		assertThat(cat.getLols(), is(nullValue()));
	}

	@Test
	public void isCapableOfDealingWithSets() throws Exception {
		ControllerMethod set = method("set", Set.class);

		requestParameterIs(set, "abc", "1", "2");

		Set<Long> abc = getFirstParameterFor(set);

		assertThat(abc, hasSize(2));
		assertThat(abc, allOf(hasItem(1l), hasItem(2l)));
	}

	@Test
	public void isCapableOfDealingWithSetsOfObjects() throws Exception {
		ControllerMethod set = method("setOfObject", Set.class);

		requestParameterIs(set, "abc.x", "1");

		Set<ABC> abc = getFirstParameterFor(set);

		assertThat(abc, hasSize(1));
		assertThat(abc.iterator().next().getX(), is(1l));
	}

	@Test
	public void shouldInjectOnlyAttributesWithSameType() throws Exception {
		Result result = mock(Result.class);
		when(request.getAttribute("result")).thenReturn(result);
		when(request.getParameterMap()).thenReturn(singletonMap("result", new String[] { "buggy" }));

		ControllerMethod method = DefaultControllerMethod.instanceFor(OtherResource.class, 
				OtherResource.class.getDeclaredMethod("logic", String.class));

		Object[] out = provider.getParametersFor(method, errors);

		assertThat(out[0], is(not((Object) result)));
		assertThat(out[0], is((Object) "buggy"));
	}
	
	@Test
	public void isCapableOfClassesThatHaveConverters() throws Exception {
		ControllerMethod abcMethod = method("abc", ABC.class);
		
		when(converters.existsFor(ABC.class)).thenReturn(true);
		when(converters.to(ABC.class)).thenReturn(new ABCConverter());

		requestParametersAre(abcMethod, ImmutableMap.of("abc.x", new String[]{ "1" }, "abc.y", new String[]{ "2" }));
		
		ABC abc = getFirstParameterFor(abcMethod);

		assertThat(abc.getX(), is(1l));
		assertThat(abc.getY(), is(2l));
	}

	private void thereAreNoParameters() {
		when(request.getParameterMap()).thenReturn(Collections.<String, String[]> emptyMap());
	}

	private void requestParametersAre(ControllerMethod method, Map<String, String[]> map) {
		when(request.getParameterMap()).thenReturn(map);
	}

	private void requestParameterIs(ControllerMethod method, String paramName, String... values) {
		requestParametersAre(method, ImmutableMap.of(paramName, values));
	}

	@SuppressWarnings("unchecked")
	private <T> T getFirstParameterFor(ControllerMethod method) {
		return (T) provider.getParametersFor(method, errors)[0];
	}

	private ParametersProvider getProvider() {
		VRaptorInstantiator instantiator = new VRaptorInstantiator(converters, new VRaptorDependencyProvider(container),
				new VRaptorParameterNamesProvider(nameProvider), request);
		instantiator.createInstantiator();
		return new IogiParametersProvider(nameProvider, request, instantiator);
	}

	private ControllerMethod method(String methodName, Class<?>... argTypes) throws NoSuchMethodException {
		return DefaultControllerMethod.instanceFor(MyResource.class, MyResource.class.getDeclaredMethod(methodName, argTypes));
	}

	//----------

	protected static class MyResource {
		public MyResource() {
		}
		void buyA(House house) {
		}
		void kick(AngryCat angryCat) {
		}
		void error(WrongCat wrongCat) {
		}
		void array(Long[] abc) {
		}
		void list(List<Long> abc) {
		}
		void listOfObject(List<ABC> abc) {
		}
		void set(Set<Long> abc) {
		}
		void setOfObject(Set<ABC> abc) {
		}
		void abc(ABC abc) {
		}
		void simple(Long xyz) {
		}
		void string(String abc) {
		}
		void stringArray(String[] abc) {
		}
		void primitive(long xyz) {
		}
		void dependency(Result result) {
		}
		void doNothing() {
		}
	}

	static class Generic<T> {
		void generic(T t) {
		}
	}

	static class Specific extends Generic<ABC> {
	}

	public static class Cat {
		private String id;
		private Long lols;

		public void setId(String id) {
			this.id = id;
		}

		public String getId() {
			return id;
		}

		public void setLols(Long lols) {
			this.lols = lols;
		}

		public Long getLols() {
			return lols;
		}
	}

	public static class House {
		private Cat cat;

		public void setCat(Cat cat) {
			this.cat = cat;
		}

		public Cat getCat() {
			return cat;
		}

		public void setExtraCats(List<Cat> extraCats) {
			this.extraCats = extraCats;
		}

		public List<Cat> getExtraCats() {
			return extraCats;
		}

		public void setIds(Long[] ids) {
			this.ids = ids;
		}

		private List<String> owners;

		public Long[] getIds() {
			return ids;
		}

		public void setOwners(List<String> owners) {
			this.owners = owners;
		}

		public List<String> getOwners() {
			return owners;
		}

		private List<Cat> extraCats;

		private Long[] ids;

	}

	public static class ABC {
		private Long x;
		private Long y;
		private List<Person> person;
		private Set<Address> addresses;
		
		public Set<Address> getAddresses() {
			return addresses;
		}

		public void setAddresses(Set<Address> addresses) {
			this.addresses = addresses;
		}

		public List<Person> getPerson() {
			return person;
		}

		public void setPerson(List<Person> person) {
			this.person = person;
		}

		public Long getX() {
			return x;
		}

		public void setX(Long x) {
			this.x = x;
		}

		public Long getY() {
			return y;
		}

		public void setY(Long y) {
			this.y = y;
		}
	}

	public static class Person {
		private String name;
		
		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
		
	}

	public static class Address {
		private String street;

		public String getStreet() {
			return street;
		}

		public void setStreet(String street) {
			this.street = street;
		}
		
	}

	public static class AngryCat {
		public void setId(String id) {
			throw new DefaultValidationException("AngryCat Exception");
		}

		public String getId() {
			throw new DefaultValidationException("AngryCat Exception");
		}
	}

	public static class WrongCat {
		public void setId(String id) {
			throw new IllegalArgumentException("AngryCat Exception"); //it isn't a ValidationException
		}

		public String getId() {
			throw new IllegalArgumentException("AngryCat Exception"); //it isn't a ValidationException
		}
	}

	class OtherResource {
		void logic(NeedsMyResource param) {
		}
		void logic(String result) {
		}
	}

	static class NeedsMyResource {
		private final MyResource myResource;

		public NeedsMyResource(MyResource myResource) {
			this.myResource = myResource;
		}

		public MyResource getMyResource() {
			return myResource;
		}
	}
}