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

import static com.google.common.io.ByteStreams.toByteArray;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadBase;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.com.caelum.vraptor.controller.ControllerMethod;
import br.com.caelum.vraptor.controller.DefaultControllerMethod;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.events.ControllerFound;
import br.com.caelum.vraptor.http.MutableRequest;
import br.com.caelum.vraptor.validator.I18nMessage;
import br.com.caelum.vraptor.validator.Validator;

/**
 * Test class for uploading features using commons-fileupload.
 *
 * @author Otávio Scherer Garcia
 */
public class CommonsUploadMultipartObserverTest {

	@Rule
	public ExpectedException exception = ExpectedException.none();

	@Rule
	public TemporaryFolder tmpdir = new TemporaryFolder();

	@Mock private InterceptorStack stack;
	@Mock private ControllerFound event;
	@Mock private MutableRequest request;
	@Mock private Validator validator;
	@Mock private ServletFileUpload servletFileUpload;

	private MultipartConfig config;
	private CommonsUploadMultipartObserver observer;

	private ControllerMethod uploadMethodController;
	private ControllerMethod uploadMethodControllerWithAnnotation;

	@Before
	public void setup() throws Exception {
		MockitoAnnotations.initMocks(this);
		config = new DefaultMultipartConfig();
		when(request.getContentType()).thenReturn("multipart/form-data");
		when(request.getMethod()).thenReturn("POST");
		observer = spy(new CommonsUploadMultipartObserver());

		Method uploadMethod = getClass().getDeclaredMethod("uploadMethod", UploadedFile.class);
		Method uploadMethodWthAnnotation = getClass().getDeclaredMethod("uploadMethodWthAnnotation", UploadedFile.class);

		uploadMethodController = DefaultControllerMethod.instanceFor(getClass(), uploadMethod);
		uploadMethodControllerWithAnnotation = DefaultControllerMethod.instanceFor(getClass(), uploadMethodWthAnnotation);
	}

	@Test
	public void shouldNotAcceptFormURLEncoded() {
		MultipartConfig config = spy(new DefaultMultipartConfig());
		when(request.getContentType()).thenReturn("application/x-www-form-urlencoded");
		when(request.getMethod()).thenReturn("POST");

		observer.upload(event, request, config, validator);

		verifyZeroInteractions(config);
	}

	@Test
	public void withFieldsOnly() throws Exception {
		when(event.getMethod()).thenReturn(uploadMethodController);

		final List<FileItem> elements = new ArrayList<>();
		elements.add(new MockFileItem("foo", "blah"));
		elements.add(new MockFileItem("bar", "blah blah"));

		when(observer.createServletFileUpload(config)).thenReturn(servletFileUpload);
		when(servletFileUpload.parseRequest(request)).thenReturn(elements);
		when(request.getCharacterEncoding()).thenReturn("UTF-8");

		observer.upload(event, request, config, validator);

		verify(request).setParameter("foo", "blah");
		verify(request).setParameter("bar", "blah blah");
	}

	@Test
	public void withFieldsOnlyWithInvalidCharset() throws Exception {
		when(event.getMethod()).thenReturn(uploadMethodController);

		final List<FileItem> elements = new ArrayList<>();
		elements.add(new MockFileItem("foo", "blah"));
		elements.add(new MockFileItem("bar", "blah blah"));

		when(observer.createServletFileUpload(config)).thenReturn(servletFileUpload);
		when(servletFileUpload.parseRequest(request)).thenReturn(elements);
		when(request.getCharacterEncoding()).thenReturn("BLAH");

		observer.upload(event, request, config, validator);

		verify(request).setParameter("foo", "blah");
		verify(request).setParameter("bar", "blah blah");
	}

	@Test
	public void withFilesAndFields() throws Exception {
		when(event.getMethod()).thenReturn(uploadMethodController);

		final List<FileItem> elements = new ArrayList<>();
		elements.add(new MockFileItem("foo", "blah"));
		elements.add(new MockFileItem("bar", "blah blah"));
		elements.add(new MockFileItem("thefile0", "foo.txt", "foo".getBytes()));
		elements.add(new MockFileItem("thefile1", "bar.txt", "bar".getBytes()));

		when(observer.createServletFileUpload(config)).thenReturn(servletFileUpload);
		when(servletFileUpload.parseRequest(request)).thenReturn(elements);

		observer.upload(event, request, config, validator);

		verify(request).setParameter("foo", "blah");
		verify(request).setParameter("bar", "blah blah");

		verify(request).setParameter("thefile0", "thefile0");
		verify(request).setParameter("thefile1", "thefile1");

		verify(request).setAttribute(eq("thefile0"), any(UploadedFile.class));
		verify(request).setAttribute(eq("thefile1"), any(UploadedFile.class));
	}

	@Test
	public void emptyFiles() throws Exception {
		when(event.getMethod()).thenReturn(uploadMethodController);

		final List<FileItem> elements = new ArrayList<>();
		elements.add(new MockFileItem("thefile0", "", new byte[0]));

		when(observer.createServletFileUpload(config)).thenReturn(servletFileUpload);
		when(servletFileUpload.parseRequest(request)).thenReturn(elements);

		observer.upload(event, request, config, validator);
	}

	@Test
	public void fieldsWithSameName() throws Exception {
		when(event.getMethod()).thenReturn(uploadMethodController);

		final List<FileItem> elements = new ArrayList<>();
		elements.add(new MockFileItem("myfile0", "foo.txt", "foo".getBytes()));
		elements.add(new MockFileItem("myfile1", "foo.txt", "bar".getBytes()));

		when(observer.createServletFileUpload(config)).thenReturn(servletFileUpload);
		when(servletFileUpload.parseRequest(request)).thenReturn(elements);

		observer.upload(event, request, config, validator);

		verify(request).setParameter("myfile0", "myfile0");
		verify(request).setParameter("myfile1", "myfile1");

		verify(request).setAttribute(eq("myfile0"), any(UploadedFile.class));
		verify(request).setAttribute(eq("myfile1"), any(UploadedFile.class));
	}

	@Test
	public void multipleUpload() throws Exception {
		when(event.getMethod()).thenReturn(uploadMethodController);

		final List<FileItem> elements = new ArrayList<>();
		elements.add(new MockFileItem("myfile0[]", "foo.txt", "foo".getBytes()));
		elements.add(new MockFileItem("myfile0[]", "foo.txt", "bar".getBytes()));

		when(observer.createServletFileUpload(config)).thenReturn(servletFileUpload);
		when(servletFileUpload.parseRequest(request)).thenReturn(elements);

		observer.upload(event, request, config, validator);

		verify(request).setParameter("myfile0[0]", "myfile0[0]");
		verify(request).setParameter("myfile0[1]", "myfile0[1]");

		verify(request).setAttribute(eq("myfile0[0]"), any(UploadedFile.class));
		verify(request).setAttribute(eq("myfile0[1]"), any(UploadedFile.class));
	}

	@Test
	public void doNothingWhenFileUploadExceptionOccurs() throws Exception {
		when(event.getMethod()).thenReturn(uploadMethodController);

		when(observer.createServletFileUpload(config)).thenReturn(servletFileUpload);
		when(servletFileUpload.parseRequest(request)).thenThrow(new FileUploadException());

		observer.upload(event, request, config, validator);
	}

	@Test
	public void shouldValidateWhenSizeLimitExceededExceptionOccurs() throws Exception {
		when(event.getMethod()).thenReturn(uploadMethodController);

		when(observer.createServletFileUpload(config)).thenReturn(servletFileUpload);
		when(servletFileUpload.parseRequest(request)).thenThrow(new FileUploadBase.SizeLimitExceededException("", 3L, 2L));

		observer.upload(event, request, config, validator);

		verify(validator).add(any(I18nMessage.class));
	}

	@Test
	public void handleValidatorMessageWhenFileUploadExceptionOccurs() throws Exception {
		when(event.getMethod()).thenReturn(uploadMethodController);

		when(observer.createServletFileUpload(config)).thenReturn(servletFileUpload);
		when(servletFileUpload.parseRequest(request)).thenThrow(new FileUploadException());
		
		observer.upload(event, request, config, validator);

		verify(validator).add(any(I18nMessage.class));
	}

	@Test
	public void shouldValidateWhenSizeLimitExceededExceptionOccursFromAnnotation() throws Exception {
		when(event.getMethod()).thenReturn(uploadMethodControllerWithAnnotation);

		when(observer.createServletFileUpload(config)).thenReturn(servletFileUpload);
		when(servletFileUpload.parseRequest(request)).thenThrow(new FileUploadBase.SizeLimitExceededException("", 3L, 2L));

		observer.upload(event, request, config, validator);

		verify(servletFileUpload).setFileSizeMax(10);
		verify(servletFileUpload).setSizeMax(20);
	}

	@Test
	public void shouldCreateDirInsideAppIfTempDirAreNotAvailable() throws Exception {
		when(event.getMethod()).thenReturn(uploadMethodController);

		DefaultMultipartConfig configSpy = (DefaultMultipartConfig) spy(config);
		doReturn(null).when(configSpy).getTemporaryDirectory();

		final List<FileItem> elements = new ArrayList<>();
		elements.add(new MockFileItem("myfile", "foo.txt", "bar".getBytes()));

		when(observer.createServletFileUpload(config)).thenReturn(servletFileUpload);
		when(servletFileUpload.parseRequest(request)).thenReturn(elements);

		observer.upload(event, request, configSpy, validator);

		verify(configSpy).createDirInsideApplication();
	}

	@Test
	public void checkIfFileHasBeenUploaded() throws Exception {
		when(event.getMethod()).thenReturn(uploadMethodController);

		final List<FileItem> elements = new ArrayList<>();
		byte[] content = "foo".getBytes();
		elements.add(new MockFileItem("thefile0", "text/plain", "file.txt", content));

		when(observer.createServletFileUpload(config)).thenReturn(servletFileUpload);
		when(servletFileUpload.parseRequest(request)).thenReturn(elements);

		observer.upload(event, request, config, validator);

		ArgumentCaptor<UploadedFile> argument = ArgumentCaptor.forClass(UploadedFile.class);
		verify(request).setAttribute(anyString(), argument.capture());

		UploadedFile file = argument.getValue();
		assertThat(file.getFileName(), is("file.txt"));
		assertThat(file.getContentType(), is("text/plain"));
		assertThat(toByteArray(file.getFile()), is(content));
	}

	@Test
	public void mustConvertUnixPathToFileName() throws Exception {
		when(event.getMethod()).thenReturn(uploadMethodController);

		List<FileItem> elements = new ArrayList<>();
		elements.add(new MockFileItem("thefile0", "text/plain", "/unix/path/file0.txt", new byte[0]));

		when(observer.createServletFileUpload(config)).thenReturn(servletFileUpload);
		when(servletFileUpload.parseRequest(request)).thenReturn(elements);

		observer.upload(event, request, config, validator);

		ArgumentCaptor<UploadedFile> argument = ArgumentCaptor.forClass(UploadedFile.class);
		verify(request).setAttribute(anyString(), argument.capture());

		assertThat(argument.getValue().getFileName(), is("file0.txt"));
	}

	@Test
	public void mustConvertWindowsPathToFileName() throws Exception {
		when(event.getMethod()).thenReturn(uploadMethodController);

		List<FileItem> elements = new ArrayList<>();
		elements.add(new MockFileItem("thefile0", "text/plain", "c:/windows/path/file0.txt", new byte[0]));

		when(observer.createServletFileUpload(config)).thenReturn(servletFileUpload);
		when(servletFileUpload.parseRequest(request)).thenReturn(elements);

		observer.upload(event, request, config, validator);

		ArgumentCaptor<UploadedFile> argument = ArgumentCaptor.forClass(UploadedFile.class);
		verify(request).setAttribute(anyString(), argument.capture());

		assertThat(argument.getValue().getFileName(), is("file0.txt"));
	}

	@Test
	public void ensureCopyUploadedFileToOutputStream() throws IOException {
		File outputFile = tmpdir.newFile();
		OutputStream outputStream = new FileOutputStream(outputFile);

		byte[] byteOnlyFileContent = { 0x0, 0x1, 0x2 };
		FileItem mockedFileItem = mock(FileItem.class);
		when(mockedFileItem.getInputStream()).thenReturn(new ByteArrayInputStream(byteOnlyFileContent));

		UploadedFile file = new CommonsUploadedFile(mockedFileItem);
		file.writeTo(outputStream);

		assertThat(outputFile.length(), is(new Long(byteOnlyFileContent.length)));
		assertThat(Files.readAllBytes(outputFile.toPath()), is(byteOnlyFileContent));
	}

	public void uploadMethod(UploadedFile file) {
	}

	@UploadSizeLimit(fileSizeLimit = 10, sizeLimit = 20)
	public void uploadMethodWthAnnotation(UploadedFile file) {
	}
}
