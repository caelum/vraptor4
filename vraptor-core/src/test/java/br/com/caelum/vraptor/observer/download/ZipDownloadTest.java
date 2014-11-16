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
package br.com.caelum.vraptor.observer.download;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class ZipDownloadTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	private Path inpuFile0;
	private Path inpuFile1;

	private @Mock HttpServletResponse response;
	private @Mock ServletOutputStream socketStream;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		inpuFile0 = folder.newFile().toPath();
		inpuFile1 = folder.newFile().toPath();

		when(response.getOutputStream()).thenReturn(socketStream);
	}

	@Test
	public void builderShouldThrowsExceptionIfFileDoesntExists() throws Exception {
		thrown.expect(NoSuchFileException.class);

		Download download = new ZipDownload("file.zip", Paths.get("/path/that/doesnt/exists/picture.jpg"));
		download.write(response);
	}

	@Test
	public void shouldUseHeadersToHttpResponse() throws IOException {
		Download fd = new ZipDownload("download.zip", inpuFile0, inpuFile1);
		fd.write(response);

		verify(response, times(1)).setHeader("Content-type", "application/zip");
		verify(response, times(1)).setHeader("Content-disposition", "attachment; filename=download.zip");
	}

	@Test
	public void testConstructWithDownloadBuilder() throws Exception {
		Download fd = DownloadBuilder.of(asList(inpuFile0, inpuFile1))
				.withFileName("download.zip")
				.downloadable().build();
		fd.write(response);

		verify(response).setHeader("Content-disposition", "attachment; filename=download.zip");
	}
}
