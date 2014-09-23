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
package br.com.caelum.vraptor.interceptor;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.Matchers.hasItems;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import br.com.caelum.vraptor.Intercepts;

public class TopologicalSortedInterceptorRegistryTest {

	@Rule
	public ExpectedException exception = ExpectedException.none();

	@Intercepts
	static interface A {}

	@Intercepts(before=A.class)
	static interface B {}

	@Intercepts(after=A.class)
	static interface C {}

	@Intercepts(after=A.class, before=C.class)
	static interface D {}

	@Intercepts(before=A.class, after=C.class)
	static interface E {}

	@Intercepts
	static interface F {}

	@Test
	public void returnsRegisteredClasses() throws Exception {
		TopologicalSortedInterceptorRegistry set = new TopologicalSortedInterceptorRegistry();
		set.register(A.class, B.class, C.class);
		List<Class<?>> list = set.all();

		assertThat(list, hasItems(new Class<?>[] { A.class, B.class, C.class }));
	}

	@Test
	public void respectsAfterAttribute() throws Exception {
		TopologicalSortedInterceptorRegistry set = new TopologicalSortedInterceptorRegistry();
		set.register(A.class);
		set.register(C.class);
		assertThat(set.all(), hasRelativeOrder(A.class, C.class));

		set = new TopologicalSortedInterceptorRegistry();
		set.register(C.class);
		set.register(A.class);
		assertThat(set.all(), hasRelativeOrder(A.class, C.class));

	}

	@Test
	public void respectsBeforeAndAfterAttribute() throws Exception {
		TopologicalSortedInterceptorRegistry set = new TopologicalSortedInterceptorRegistry();
		set.register(A.class);
		set.register(C.class);
		set.register(D.class);
		assertThat(set.all(), hasRelativeOrder(A.class, D.class, C.class));

		set = new TopologicalSortedInterceptorRegistry();
		set.register(C.class);
		set.register(D.class);
		set.register(A.class);
		assertThat(set.all(), hasRelativeOrder(A.class, D.class, C.class));

	}

	@Test
	public void failsOnCycles() throws Exception {
		exception.expect(IllegalStateException.class);
		exception.expectMessage(containsString("There is a cycle on the interceptor sequence"));

		TopologicalSortedInterceptorRegistry set = new TopologicalSortedInterceptorRegistry();
		set.register(A.class);
		set.register(C.class);
		set.register(E.class);
		set.all();
	}

	@Test
	public void respectsInsertionOrderIfNoRelationIsSet() throws Exception {
		TopologicalSortedInterceptorRegistry set = new TopologicalSortedInterceptorRegistry();
		set.register(A.class);
		set.register(F.class);
		assertThat(set.all(), hasRelativeOrder(A.class, F.class));

		set = new TopologicalSortedInterceptorRegistry();
		set.register(F.class);
		set.register(A.class);
		assertThat(set.all(), hasRelativeOrder(F.class, A.class));
	}

	@Test
	public void respectsBeforeAttribute() throws Exception {
		TopologicalSortedInterceptorRegistry set = new TopologicalSortedInterceptorRegistry();
		set.register(A.class);
		set.register(B.class);
		assertThat(set.all(), hasRelativeOrder(B.class, A.class));

		set = new TopologicalSortedInterceptorRegistry();
		set.register(B.class);
		set.register(A.class);
		assertThat(set.all(), hasRelativeOrder(B.class, A.class));

	}

	private Matcher<List<Class<?>>> hasRelativeOrder(final Class<?>... elements) {
		return new TypeSafeMatcher<List<Class<?>>>() {
			@Override
			protected void describeMismatchSafely(List<Class<?>> item, Description mismatchDescription) {
			}

			@Override
			protected boolean matchesSafely(List<Class<?>> item) {
				for (int i = 0; i < elements.length - 1; i++) {
					if (item.indexOf(elements[i]) > item.indexOf(elements[i+1])) {
						return false;
					}
				}
				return true;
			}

			@Override
			public void describeTo(Description description) {
				description.appendText("A list with relative order ").appendValue(elements);
			}
		};
	}


}
