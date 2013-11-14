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
