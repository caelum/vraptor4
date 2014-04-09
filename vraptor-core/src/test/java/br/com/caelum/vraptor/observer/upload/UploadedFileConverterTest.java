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
package br.com.caelum.vraptor.observer.upload;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.com.caelum.vraptor.observer.upload.UploadedFile;
import br.com.caelum.vraptor.observer.upload.UploadedFileConverter;

public class UploadedFileConverterTest {
	
	private @Mock HttpServletRequest request;
	private @Mock UploadedFile file;
	
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testIfUploadedFileIsConverted() {
		when(request.getAttribute("myfile")).thenReturn(file);
		
		UploadedFileConverter converter = new UploadedFileConverter(request);
		
		UploadedFile uploadedFile = converter.convert("myfile", UploadedFile.class);
		assertEquals(file, uploadedFile);
	}
}
