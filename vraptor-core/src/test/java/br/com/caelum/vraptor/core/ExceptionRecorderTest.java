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
package br.com.caelum.vraptor.core;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.verify;

import java.io.IOException;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.proxy.JavassistProxifier;
import br.com.caelum.vraptor.proxy.Proxifier;

public class ExceptionRecorderTest {
	
	static final String DEFAULT_REDIRECT = "/any-resource";

	@Mock private Result result;
	private ExceptionMapper mapper;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		Proxifier proxifier = new JavassistProxifier();
		mapper = new DefaultExceptionMapper(proxifier, new DefaultReflectionProvider());
	}

	@Test
	public void withRootException() {
		mapper.record(Exception.class).forwardTo(DEFAULT_REDIRECT);
		mapper.findByException(new Exception()).replay(result);

		verify(result).forwardTo(DEFAULT_REDIRECT);
	}

	@Test
	public void withNestedException() {
		mapper.record(IllegalStateException.class).forwardTo(DEFAULT_REDIRECT);
		mapper.findByException(new RuntimeException(new IllegalStateException())).replay(result);

		verify(result).forwardTo(DEFAULT_REDIRECT);
	}

	@Test
	public void whenNotFoundException() {
		mapper.record(IOException.class).forwardTo(DEFAULT_REDIRECT);
		ExceptionRecorder<Result> recorder = mapper.findByException(new RuntimeException(new IllegalStateException()));

		assertThat(recorder, Matchers.nullValue());
	}
}
