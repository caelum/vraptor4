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

import static org.junit.Assert.assertArrayEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class FileDownloadTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	private File file;
	private byte[] bytes;
	private @Mock HttpServletResponse response;
	private ServletOutputStream socketStream;
	private ByteArrayOutputStream outputStream;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		bytes = new byte[] { (byte) 0x0 };
		outputStream = new ByteArrayOutputStream();

		file = folder.newFile();
		Files.write(file.toPath(), bytes);
		
		socketStream = new ServletOutputStream() {
			@Override
			public void write(int b) throws IOException {
				outputStream.write(b);
			}

			@Override
			public boolean isReady() {
				return false;
			}

			@Override
			public void setWriteListener(WriteListener writeListener) {
			}
		};

		when(response.getOutputStream()).thenReturn(socketStream);
	}

	@Test
	public void shouldFlushWholeFileToHttpResponse() throws IOException {
		FileDownload fd = new FileDownload(file, "type");
		fd.write(response);

		assertArrayEquals(bytes, outputStream.toByteArray());
	}

	@Test
	public void shouldUseHeadersToHttpResponse() throws IOException {
		FileDownload fd = new FileDownload(file, "type", "x.txt", false);
		fd.write(response);
		
		verify(response, times(1)).setHeader("Content-type", "type");
		verify(response, times(1)).setHeader("Content-disposition", "inline; filename=x.txt");
		assertArrayEquals(bytes, outputStream.toByteArray());
	}

	@Test
	public void builderShouldThrowsExceptionIfFileDoesntExists() throws Exception {
		thrown.expect(FileNotFoundException.class);
		thrown.expectMessage("File picture.jpg doesn't exists");

		DownloadBuilder.of(new File("/path/that/doesnt/exists/picture.jpg")).build();
	}

	@Test
	public void builderShouldUseNameArgument() throws Exception {
		Download fd = DownloadBuilder.of(file).withFileName("file.txt")
				.withContentType("text/plain").downloadable().build();
		fd.write(response);

		verify(response).setHeader("Content-Length", String.valueOf(file.length()));
		verify(response).setHeader("Content-disposition", "attachment; filename=file.txt");
	}

	@Test
	public void builderShouldUseFileNameWhenNameNotPresent() throws Exception {
		Download fd = DownloadBuilder.of(file).withContentType("text/plain").build();
		fd.write(response);

		verify(response).setHeader("Content-Length", String.valueOf(file.length()));
		verify(response).setHeader("Content-disposition", "inline; filename=" + file.getName());
	}
}
