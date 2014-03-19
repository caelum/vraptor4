package br.com.caelum.vraptor.observer.upload;

import static com.google.common.io.ByteStreams.toByteArray;
import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadBase;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.com.caelum.vraptor.controller.ControllerMethod;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.events.ControllerFound;
import br.com.caelum.vraptor.http.InvalidParameterException;
import br.com.caelum.vraptor.http.MutableRequest;
import br.com.caelum.vraptor.validator.I18nMessage;
import br.com.caelum.vraptor.validator.Validator;

/**
 * Test class for uploading features using commons-fileupload.
 *
 * @author Otávio Scherer Garcia
 */
public class CommonsUploadMultipartObserverTest {

	@Mock private InterceptorStack stack;
	@Mock private ControllerFound event;
	@Mock private MutableRequest request;
	@Mock private Validator validator;
	@Mock private ServletFileUpload servletFileUpload;
	private MultipartConfig config;
	private CommonsUploadMultipartObserver observer;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		config = new DefaultMultipartConfig();
		when(request.getContentType()).thenReturn("multipart/form-data");
		when(request.getMethod()).thenReturn("POST");
		observer = spy(new CommonsUploadMultipartObserver());
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
		final List<FileItem> elements = new ArrayList<>();
		elements.add(new MockFileItem("foo", "blah"));
		elements.add(new MockFileItem("bar", "blah blah"));

		when(observer.createServletFileUpload(config)).thenReturn(servletFileUpload);
		when(servletFileUpload.parseRequest(request)).thenReturn(elements);

		observer.upload(event, request, config, validator);

		verify(request).setParameter("foo", "blah");
		verify(request).setParameter("bar", "blah blah");
	}

	@Test
	public void withFieldsOnlyWithInvalidCharset() throws Exception {
		final List<FileItem> elements = new ArrayList<>();
		elements.add(new MockFileItem("foo", "blah"));
		elements.add(new MockFileItem("bar", "blah blah"));

		when(observer.createServletFileUpload(config)).thenReturn(servletFileUpload);
		when(servletFileUpload.parseRequest(request)).thenReturn(elements);

		observer.upload(event, request, config, validator);

		verify(request).setParameter("foo", "blah");
		verify(request).setParameter("bar", "blah blah");
	}

	@Test
	public void withFilesAndFields() throws Exception {
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
		final List<FileItem> elements = new ArrayList<>();
		elements.add(new MockFileItem("thefile0", "", new byte[0]));

		when(observer.createServletFileUpload(config)).thenReturn(servletFileUpload);
		when(servletFileUpload.parseRequest(request)).thenReturn(elements);

		observer.upload(event, request, config, validator);
	}

	@Test(expected = InvalidParameterException.class)
	public void throwsInvalidParameterExceptionIfIOExceptionOccurs() throws Exception {
		FileItem item = new MockFileItem("thefile0", "file.txt", new byte[0]);
		item = spy(item);

		doThrow(new IOException()).when(item).getInputStream();

		when(observer.createServletFileUpload(config)).thenReturn(servletFileUpload);
		when(servletFileUpload.parseRequest(request)).thenReturn(asList(item));

		observer.upload(event, request, config, validator);
	}

	@Test
	public void fieldsWithSameName() throws Exception {
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

		when(observer.createServletFileUpload(config)).thenReturn(servletFileUpload);
		when(servletFileUpload.parseRequest(request)).thenThrow(new FileUploadException());

		observer.upload(event, request, config, validator);
	}

	@Test
	public void shouldValidateWhenSizeLimitExceededExceptionOccurs() throws Exception {
		when(observer.createServletFileUpload(config)).thenReturn(servletFileUpload);
		when(servletFileUpload.parseRequest(request)).thenThrow(new FileUploadBase.SizeLimitExceededException("", 1L, 2L));

		observer.upload(event, request, config, validator);

		verify(validator).add(any(I18nMessage.class));
	}

	@Test
	public void handleValidatorMessageWhenFileUploadExceptionOccurs() throws Exception {
		when(observer.createServletFileUpload(config)).thenReturn(servletFileUpload);
		when(servletFileUpload.parseRequest(request)).thenThrow(new FileUploadException());
		
		observer.upload(event, request, config, validator);

		verify(validator).add(any(I18nMessage.class));
	}

	@Test
	public void shouldCreateDirInsideAppIfTempDirAreNotAvailable() throws Exception {
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
		List<FileItem> elements = new ArrayList<>();
		elements.add(new MockFileItem("thefile0", "text/plain", "c:/windows/path/file0.txt", new byte[0]));

		when(observer.createServletFileUpload(config)).thenReturn(servletFileUpload);
		when(servletFileUpload.parseRequest(request)).thenReturn(elements);

		observer.upload(event, request, config, validator);

		ArgumentCaptor<UploadedFile> argument = ArgumentCaptor.forClass(UploadedFile.class);
		verify(request).setAttribute(anyString(), argument.capture());

		assertThat(argument.getValue().getFileName(), is("file0.txt"));
	}
}
