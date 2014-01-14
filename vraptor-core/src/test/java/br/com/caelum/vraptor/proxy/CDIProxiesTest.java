package br.com.caelum.vraptor.proxy;

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import javax.enterprise.inject.Specializes;

import org.junit.Test;
import org.junit.runner.RunWith;

import br.com.caelum.vraptor.WeldJunitRunner;

@RunWith(WeldJunitRunner.class)
public class CDIProxiesTest {

	@Test
	public void testWrapBean() {
		TestBean wraped = CDIProxies.unwrap(TestBean.class);
		assertThat(wraped, is(instanceOf(TestBean.class)));
	}

	@Test
	public void testWrapAlternativeBean() {
		TestBean wraped = CDIProxies.unwrap(AlternativeTestBean.class);
		assertThat(wraped, is(instanceOf(AlternativeTestBean.class)));
		assertThat(wraped.getClass().getName(), is(AlternativeTestBean.class.getName()));
	}

	public static class TestBean {}

	@Specializes
	public static class AlternativeTestBean extends TestBean {}
}
