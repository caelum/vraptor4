package br.com.caelum.vraptor.interceptor;

import static org.hamcrest.Matchers.hasItems;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Test;

import br.com.caelum.vraptor.Intercepts;

public class TopologicalSortedInterceptorRegistryTest {

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

	@Test(expected=IllegalStateException.class)
	public void failsOnCycles() throws Exception {
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
	public void usesDefaultInterceptorsIfNoRelationIsSet() throws Exception {
		TopologicalSortedInterceptorRegistry set = new TopologicalSortedInterceptorRegistry();
		set.register(A.class);
		assertThat(set.all(), hasRelativeOrder(ControllerLookupInterceptor.class, A.class, ExecuteMethodInterceptor.class));

		set = new TopologicalSortedInterceptorRegistry();
		set.register(F.class);
		assertThat(set.all(), hasRelativeOrder(ControllerLookupInterceptor.class, F.class, ExecuteMethodInterceptor.class));
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
