package br.com.caelum.vraptor.musicjungle.converter;

import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;

import br.com.caelum.vraptor.Convert;
import br.com.caelum.vraptor.Converter;
import br.com.caelum.vraptor.interceptor.multipart.UploadedFile;

/**
 * VRaptor's file upload converter.
 *
 */
@Convert(UploadedFile.class)
public class UploadedFileConverter implements Converter<UploadedFile> {

	private final HttpServletRequest request;

	public UploadedFileConverter(HttpServletRequest request) {
		this.request = request;
	}

	@Override
	public UploadedFile convert(String value, Class<? extends UploadedFile> type, ResourceBundle bundle) {
		Object upload = request.getAttribute(value);
		return upload == null ? null : type.cast(upload);
	}
}