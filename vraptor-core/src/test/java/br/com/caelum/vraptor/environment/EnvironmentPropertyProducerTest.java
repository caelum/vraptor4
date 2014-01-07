package br.com.caelum.vraptor.environment;

import static org.junit.Assert.assertEquals;

import java.util.NoSuchElementException;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;

import br.com.caelum.vraptor.WeldJunitRunner;

@RunWith(WeldJunitRunner.class)
public class EnvironmentPropertyProducerTest {

	@Inject @Property("email.server.host") 
	private String mailHost;

	@Inject @Property private String itWorks;

	@Inject @Property("non-existent.key") 
	private Instance<String> nonExistent;
	
	@Test
	public void shouldInjectAnEnvironmentProperty() {
		assertEquals("vraptor.caelum.com.br", mailHost);
	}
	
	@Test
	public void shouldInferKeyFromFieldName() throws Exception {
		assertEquals(itWorks, "It Works!");
	}
	
	@Test(expected=NoSuchElementException.class)
	public void shouldNotResolveUnexistentKeys() throws Exception {
		nonExistent.get();
	}
}