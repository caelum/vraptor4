package br.com.caelum.vraptor.interceptor.multipart;

import static com.google.common.io.ByteStreams.toByteArray;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadBase;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.com.caelum.vraptor.Validator;
import br.com.caelum.vraptor.controller.ControllerMethod;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.http.MutableRequest;
import br.com.caelum.vraptor.validator.I18nMessage;

/**
 * Test class for uploading features using commons-fileupload.
 *
 * @author Otávio Scherer Garcia
 */
public class CommonsUploadMultipartInterceptorTest {

	@Mock private InterceptorStack stack;
	@Mock private ControllerMethod method;
	@Mock private MutableRequest request;
	@Mock private Validator validator;
	private CommonsUploadMultipartInterceptor interceptor;
	private ServletFileUpload uploader;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		uploader = mock(ServletFileUpload.class);
	}

	@Test
	public void shouldNotAcceptFormURLEncoded() {
		interceptor = new CommonsUploadMultipartInterceptor(request, validator, uploader);

		when(request.getContentType()).thenReturn("application/x-www-form-urlencoded");
		when(request.getMethod()).thenReturn("POST");

		assertThat(interceptor.accepts(method), equalTo(false));
	}

	@Test
	public void shouldAcceptMultipart() {
		interceptor = new CommonsUploadMultipartInterceptor(request, validator, uploader);

		when(request.getContentType()).thenReturn("multipart/form-data");
		when(request.getMethod()).thenReturn("POST");

		assertThat(interceptor.accepts(method), equalTo(true));
	}

	@Test
	public void withFieldsOnly() throws Exception {
		interceptor = new CommonsUploadMultipartInterceptor(request, validator, uploader);

		final List<FileItemStream> elements = new ArrayList<>();
		elements.add(new MockFileItem("foo", "blah"));
		elements.add(new MockFileItem("bar", "blah blah"));

		when(request.getCharacterEncoding()).thenReturn("utf-8");
		when(request.getContentType()).thenReturn("multipart/form-data");
		when(request.getMethod()).thenReturn("POST");
		when(uploader.getItemIterator(request)).thenReturn(new MockFileItemIterator(elements));

		interceptor.intercept(stack, null, null);

		verify(request).setParameter("foo", "blah");
		verify(request).setParameter("bar", "blah blah");
	}

	@Test
	public void withFilesAndFields() throws Exception {
		final List<FileItemStream> elements = new ArrayList<>();
		elements.add(new MockFileItem("foo", "blah"));
		elements.add(new MockFileItem("bar", "blah blah"));
		elements.add(new MockFileItem("thefile0", "foo.txt", "foo".getBytes()));
		elements.add(new MockFileItem("thefile1", "bar.txt", "bar".getBytes()));

		interceptor = new CommonsUploadMultipartInterceptor(request, validator, uploader);

		when(request.getContentType()).thenReturn("multipart/form-data");
		when(request.getMethod()).thenReturn("POST");
		when(uploader.getItemIterator(request)).thenReturn(new MockFileItemIterator(elements));

		interceptor.intercept(stack, null, null);

		verify(request).setParameter("foo", "blah");
		verify(request).setParameter("bar", "blah blah");

		verify(request).setParameter("thefile0", "thefile0");
		verify(request).setParameter("thefile1", "thefile1");

		verify(request).setAttribute(eq("thefile0"), any(UploadedFile.class));
		verify(request).setAttribute(eq("thefile1"), any(UploadedFile.class));
	}

	@Test
	public void emptyFiles() throws Exception {
		interceptor = new CommonsUploadMultipartInterceptor(request, validator, uploader);

		final List<FileItemStream> elements = new ArrayList<>();
		elements.add(new MockFileItem("thefile0", "", new byte[0]));

		when(request.getContentType()).thenReturn("multipart/form-data");
		when(request.getMethod()).thenReturn("POST");
		when(uploader.getItemIterator(request)).thenReturn(new MockFileItemIterator(elements));

		interceptor.intercept(stack, null, null);
	}

	@Test
	public void fieldsWithSameName() throws Exception {
		interceptor = new CommonsUploadMultipartInterceptor(request, validator, uploader);

		final List<FileItemStream> elements = new ArrayList<>();
		elements.add(new MockFileItem("myfile0", "foo.txt", "foo".getBytes()));
		elements.add(new MockFileItem("myfile1", "foo.txt", "bar".getBytes()));

		when(request.getContentType()).thenReturn("multipart/form-data");
		when(request.getMethod()).thenReturn("POST");
		when(uploader.getItemIterator(request)).thenReturn(new MockFileItemIterator(elements));

		interceptor.intercept(stack, null, null);

		verify(request).setParameter("myfile0", "myfile0");
		verify(request).setParameter("myfile1", "myfile1");

		verify(request).setAttribute(eq("myfile0"), any(UploadedFile.class));
		verify(request).setAttribute(eq("myfile1"), any(UploadedFile.class));
	}

	@Test
	public void multipleUpload() throws Exception {
		interceptor = new CommonsUploadMultipartInterceptor(request, validator, uploader);

		final List<FileItemStream> elements = new ArrayList<>();
		elements.add(new MockFileItem("myfile0[]", "foo.txt", "foo".getBytes()));
		elements.add(new MockFileItem("myfile0[]", "foo.txt", "bar".getBytes()));

		when(request.getContentType()).thenReturn("multipart/form-data");
		when(request.getMethod()).thenReturn("POST");
		when(uploader.getItemIterator(request)).thenReturn(new MockFileItemIterator(elements));

		interceptor.intercept(stack, null, null);

		verify(request).setParameter("myfile0[0]", "myfile0[0]");
		verify(request).setParameter("myfile0[1]", "myfile0[1]");

		verify(request).setAttribute(eq("myfile0[0]"), any(UploadedFile.class));
		verify(request).setAttribute(eq("myfile0[1]"), any(UploadedFile.class));
	}

	@Test
	public void shouldValidateWhenSizeLimitExceededExceptionOccurs() throws Exception {
		interceptor = new CommonsUploadMultipartInterceptor(request, validator, uploader);

		when(uploader.getItemIterator(request))
			.thenThrow(new FileUploadBase.SizeLimitExceededException("", 0L, 0L));

		interceptor.intercept(stack, null, null);

		verify(validator).add(any(I18nMessage.class));
	}

	@Test
	public void checkIfFileHasBeenUploaded() throws Exception {
		final List<FileItemStream> elements = new ArrayList<>();
		byte[] content = "foo".getBytes();
		elements.add(new MockFileItem("thefile0", "text/plain", "file.txt", content));
		
		when(request.getContentType()).thenReturn("multipart/form-data");
		when(request.getMethod()).thenReturn("POST");
		when(uploader.getItemIterator(request)).thenReturn(new MockFileItemIterator(elements));
		
		interceptor = new CommonsUploadMultipartInterceptor(request, validator, uploader);
		
		interceptor.intercept(stack, null, null);

		ArgumentCaptor<UploadedFile> argument = ArgumentCaptor.forClass(UploadedFile.class);
		verify(request).setAttribute(anyString(), argument.capture());

		UploadedFile file = argument.getValue();
		assertThat(file.getFileName(), is("file.txt"));
		assertThat(file.getContentType(), is("text/plain"));
		assertThat(toByteArray(file.getContent()), is(content));
	}
}
