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
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Set;

import org.junit.Test;

import br.com.caelum.iogi.parameters.Parameter;
import br.com.caelum.iogi.parameters.Parameters;
import br.com.caelum.iogi.reflection.Target;
import br.com.caelum.vraptor.controller.ControllerMethod;
import br.com.caelum.vraptor.controller.DefaultControllerMethod;
import br.com.caelum.vraptor.http.ParametersProvider;
import br.com.caelum.vraptor.http.ParametersProviderTest;

public class IogiParametersProviderTest extends ParametersProviderTest {

	@Override
	protected ParametersProvider getProvider() {
		VRaptorInstantiator instantiator = new VRaptorInstantiator(converters, new VRaptorDependencyProvider(container), new VRaptorParameterNamesProvider(nameProvider), request);
		instantiator.createInstantiator();
		
		return new IogiParametersProvider(nameProvider, request, instantiator);
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
		final Parameters expectedParamters = new Parameters(
				Arrays.asList(new Parameter("name", "a"), new Parameter("name", "b")));

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
		requestParameterIs(setId, "id", "asdf");

		getParameters(setId);

		assertThat(errors.size(), is(1));
		assertThat(errors.get(0), hasMessage("asdf is not a valid integer."));
		assertThat(errors.get(0).getCategory(), is("id"));
	}

	@Test
	public void inCaseOfConversionErrorsOnlyNullifyTheProblematicParameter() throws Exception {
		ControllerMethod setId = DefaultControllerMethod.instanceFor(House.class, House.class.getMethod("setCat", Cat.class));
		requestParameterIs(setId, "cat.lols", "sad kitten");

		Cat cat = getParameters(setId);
		assertThat(cat, is(notNullValue()));
		assertThat(cat.getLols(), is(nullValue()));
	}

	@Test
	public void isCapableOfDealingWithSets() throws Exception {
		when(nameProvider.parameterNamesFor(any(Method.class))).thenReturn(new String[]{"abc"});

		ControllerMethod set = method("set", Set.class);

		requestParameterIs(set, "abc", "1", "2");

		Set<Long> abc = getParameters(set);

		assertThat(abc, hasSize(2));
		assertThat(abc, allOf(hasItem(1l), hasItem(2l)));
	}

	@Test
	public void isCapableOfDealingWithSetsOfObjects() throws Exception {
		when(nameProvider.parameterNamesFor(any(Method.class))).thenReturn(new String[]{"abc"});

		ControllerMethod set = method("setOfObject", Set.class);

		requestParameterIs(set, "abc.x", "1");

		Set<ABC> abc = getParameters(set);

		assertThat(abc, hasSize(1));
		assertThat(abc.iterator().next().getX(), is(1l));
	}

	//----------

	class OtherResource {
		void logic(NeedsMyResource param) {
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