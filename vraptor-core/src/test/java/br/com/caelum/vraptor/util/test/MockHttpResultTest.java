package br.com.caelum.vraptor.util.test;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor4.util.test.MockHttpResult;
import br.com.caelum.vraptor4.view.Results;

public class MockHttpResultTest {
	private MockHttpResult result;
	
	@Before
	public void setUp() throws Exception {
		result = new MockHttpResult();
	}
	
	@Test
	public void test() throws Exception {
		result.use(Results.http()).body("content");
		Assert.assertEquals("content", result.getBody());
	}
}
