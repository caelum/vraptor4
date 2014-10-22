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
package br.com.caelum.vraptor.serialization;

import static java.util.Collections.sort;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.inject.Instance;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.core.DefaultReflectionProvider;
import br.com.caelum.vraptor.http.FormatResolver;
import br.com.caelum.vraptor.other.pack4ge.DumbSerialization;
import br.com.caelum.vraptor.serialization.gson.GsonJSONSerialization;
import br.com.caelum.vraptor.serialization.xstream.XStreamXMLSerialization;
import br.com.caelum.vraptor.util.test.MockInstanceImpl;
import br.com.caelum.vraptor.view.PageResult;
import br.com.caelum.vraptor.view.Status;

public class DefaultRepresentationResultTest {

	private @Mock FormatResolver formatResolver;
	private @Mock Serialization serialization;
	private @Mock Result result;
	private @Mock PageResult pageResult;
	private @Mock Status status;

	private RepresentationResult representation;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		when(result.use(PageResult.class)).thenReturn(pageResult);
		when(result.use(Status.class)).thenReturn(status);
		Instance<Serialization> serializations = new MockInstanceImpl<>(serialization);
		representation = new DefaultRepresentationResult(formatResolver, result, serializations);
	}

	@Test
	public void whenThereIsNoFormatGivenShouldForwardToDefaultPage() {
		when(formatResolver.getAcceptFormat()).thenReturn(null);

		Serializer serializer = representation.from(new Object());

		assertThat(serializer, is(instanceOf(IgnoringSerializer.class)));

		verify(status).notAcceptable();
	}

	@Test
	public void shouldSend404IfNothingIsRendered() {
		when(formatResolver.getAcceptFormat()).thenReturn(null);

		Serializer serializer = representation.from(null);

		assertThat(serializer, is(instanceOf(IgnoringSerializer.class)));

		verify(status).notFound();
	}

	@Test
	public void whenThereIsNoFormatGivenShouldForwardToDefaultPageWithAlias() {
		when(formatResolver.getAcceptFormat()).thenReturn(null);

		Object object = new Object();
		Serializer serializer = representation.from(object, "Alias!");

		assertThat(serializer, is(instanceOf(IgnoringSerializer.class)));

		verify(status).notAcceptable();
	}

	@Test
	public void whenThereIsAFormatGivenShouldUseCorrectSerializer() {
		when(formatResolver.getAcceptFormat()).thenReturn("xml");

		when(serialization.accepts("xml")).thenReturn(true);
		Object object = new Object();

		representation.from(object);

		verify(serialization).from(object);
	}

	@Test
	public void whenThereIsAFormatGivenShouldUseCorrectSerializerWithAlias() {
		when(formatResolver.getAcceptFormat()).thenReturn("xml");

		when(serialization.accepts("xml")).thenReturn(true);
		Object object = new Object();

		representation.from(object, "Alias!");

		verify(serialization).from(object, "Alias!");
	}

	@Test
	public void whenSerializationDontAcceptsFormatItShouldntBeUsed() {
		when(formatResolver.getAcceptFormat()).thenReturn("xml");

		when(serialization.accepts("xml")).thenReturn(false);
		Object object = new Object();

		representation.from(object);

		verify(serialization, never()).from(object);
	}

	@Test
	public void shouldSortBasedOnPackageNamesLessPriorityToCaelumInitialList3rdPartyFirst() {
		List<Serialization> serializers = new ArrayList<>();

		serializers.add(new DumbSerialization());
		serializers.add(new XStreamXMLSerialization(null, null, null));
		serializers.add(new GsonJSONSerialization(null, null, null, null, new DefaultReflectionProvider()));
		serializers.add(new HTMLSerialization(null, null));

		sort(serializers, new DefaultRepresentationResult.ApplicationPackageFirst());

		assertEquals("br.com.caelum.vraptor.other.pack4ge", serializers.get(0).getClass().getPackage().getName());

	}

	@Test
	public void shouldSortBasedOnPackageNamesLessPriorityToCaelumInitialList3rdPartyLast() {
		List<Serialization> serializers = new ArrayList<>();

		serializers.add(new XStreamXMLSerialization(null, null, null));
		serializers.add(new GsonJSONSerialization(null, null, null, null, new DefaultReflectionProvider()));
		serializers.add(new HTMLSerialization(null, null));
		serializers.add(new DumbSerialization());

		sort(serializers, new DefaultRepresentationResult.ApplicationPackageFirst());

		assertEquals("br.com.caelum.vraptor.other.pack4ge", serializers.get(0).getClass().getPackage().getName());
	}
}
