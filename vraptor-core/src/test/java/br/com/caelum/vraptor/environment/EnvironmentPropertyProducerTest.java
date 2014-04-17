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
	
	@Inject @Property(defaultValue = "default") 
	private String defaultValue;
	
	@Inject @Property(defaultValue = "default") 
	private String ignoreDefaultValue;
	
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
	
	@Test
	public void shouldResultDefaultValue() throws Exception {
		assertEquals("default", defaultValue);
	}
	
	@Test
	public void shouldResultValueOfProperties() throws Exception {
		assertEquals("should ignore default value!", ignoreDefaultValue);
	}
}