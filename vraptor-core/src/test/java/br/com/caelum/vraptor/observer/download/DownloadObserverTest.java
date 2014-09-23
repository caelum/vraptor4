/***
 * Copyright (c) 2009 Caelum - www.caelum.com.br/opensource
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package br.com.caelum.vraptor.observer.download;

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.util.Arrays;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.controller.ControllerMethod;
import br.com.caelum.vraptor.core.MethodInfo;
import br.com.caelum.vraptor.events.MethodExecuted;

public class DownloadObserverTest {

	@Rule
	public TemporaryFolder tmpdir = new TemporaryFolder();
	
	private DownloadObserver downloadObserver;

	@Mock private MethodInfo methodInfo;
	@Mock private HttpServletResponse response;
	@Mock private ControllerMethod controllerMethod;
	@Mock private ServletOutputStream outputStream;
	@Mock private Result result;

	@Before
	public void setup() throws Exception {
		MockitoAnnotations.initMocks(this);
		when(response.getOutputStream()).thenReturn(outputStream);
		when(result.use(DownloadView.class)).thenReturn(new DownloadView(response));
		downloadObserver = new DownloadObserver();
	}

	@Test
	public void whenResultIsADownloadShouldUseIt() throws Exception {
		when(controllerMethod.getMethod()).thenReturn(getMethod("download"));
		Download download = mock(Download.class);
		when(methodInfo.getResult()).thenReturn(download);
		downloadObserver.download(new MethodExecuted(controllerMethod, methodInfo), result);
		verify(download).write(response);
	}

	@Test
	public void whenResultIsAnInputStreamShouldCreateAInputStreamDownload() throws Exception {
		when(controllerMethod.getMethod()).thenReturn(getMethod("asByte"));
		byte[] bytes = "abc".getBytes();
		when(methodInfo.getResult()).thenReturn(new ByteArrayInputStream(bytes));
		downloadObserver.download(new MethodExecuted(controllerMethod, methodInfo), result);
		verify(outputStream).write(argThat(is(arrayStartingWith(bytes))), eq(0), eq(3));
	}

	@Test
	public void whenResultIsAnInputStreamShouldCreateAByteArrayDownload() throws Exception {
		when(controllerMethod.getMethod()).thenReturn(getMethod("asByte"));
		byte[] bytes = "abc".getBytes();
		when(methodInfo.getResult()).thenReturn(bytes);
		downloadObserver.download(new MethodExecuted(controllerMethod, methodInfo), result);
		verify(outputStream).write(argThat(is(arrayStartingWith(bytes))), eq(0), eq(3));
	}

	@Test
	public void whenResultIsAFileShouldCreateAFileDownload() throws Exception {
		when(controllerMethod.getMethod()).thenReturn(getMethod("file"));
		File tmp = tmpdir.newFile();
		Files.write(tmp.toPath(), "abc".getBytes());
		when(methodInfo.getResult()).thenReturn(tmp);
		downloadObserver.download(new MethodExecuted(controllerMethod, methodInfo), result);
		verify(outputStream).write(argThat(is(arrayStartingWith("abc".getBytes()))), eq(0), eq(3));
		tmp.delete();
	}

	@Test
	public void whenResultIsNullShouldDoNothing() throws Exception {
		when(controllerMethod.getMethod()).thenReturn(getMethod("download"));
		when(result.used()).thenReturn(true);
		downloadObserver.download(new MethodExecuted(controllerMethod, methodInfo), result);
		verifyZeroInteractions(response);
	}

	@Test
	public void whenResultWasUsedShouldDoNothing() throws Exception {
		when(controllerMethod.getMethod()).thenReturn(getMethod("download"));
		when(methodInfo.getResult()).thenReturn(null);
		downloadObserver.download(new MethodExecuted(controllerMethod, methodInfo), result);
		verifyZeroInteractions(response);
	}

	@Test
	public void shouldNotAcceptStringReturn() throws Exception {
		assertNull("String is not a Download", downloadObserver.resolveDownload(""));
	}

	@Test
	public void shouldAcceptFile() throws Exception {
		File file = tmpdir.newFile();
		assertThat(downloadObserver.resolveDownload(file), instanceOf(FileDownload.class));
	}

	@Test
	public void shouldAcceptInput() throws Exception {
		InputStream inputStream = mock(InputStream.class);
		assertThat(downloadObserver.resolveDownload(inputStream), instanceOf(InputStreamDownload.class));
	}

	@Test
	public void shouldAcceptDownload() throws Exception {
		Download download = mock(Download.class);
		assertEquals(downloadObserver.resolveDownload(download), download);
	}

	@Test
	public void shouldAcceptByte() throws Exception {
		assertThat(downloadObserver.resolveDownload(new byte[]{}), instanceOf(ByteArrayDownload.class));
	}

	private Matcher<byte[]> arrayStartingWith(final byte[] array) {
		return new TypeSafeMatcher<byte[]>() {
			@Override
			protected void describeMismatchSafely(byte[] item, Description mismatchDescription) {
			}

			@Override
			protected boolean matchesSafely(byte[] item) {
				if (item.length < array.length) {
					return false;
				}
				for (int i = 0; i < array.length; i++) {
					if (array[i] != item[i]) {
						return false;
					}
				}
				return true;
			}

			@Override
			public void describeTo(Description description) {
				description.appendText("a byte array starting with " + Arrays.toString(array));
			}
		};
	}

	static class FakeController {
		public String string() {
			return null;
		}
		public File file() {
			return null;
		}
		public InputStream input() {
			return null;
		}
		public Download download() {
			return null;
		}
		public byte[] asByte() {
			return null;
		}
	}

	private Method getMethod(String methodName) throws NoSuchMethodException {
		return FakeController.class.getMethod(methodName);
	}
}
