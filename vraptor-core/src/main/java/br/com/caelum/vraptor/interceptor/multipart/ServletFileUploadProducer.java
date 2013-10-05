package br.com.caelum.vraptor.interceptor.multipart;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;

import org.apache.commons.fileupload.servlet.ServletFileUpload;

/**
 * A producer for {@link ServletFileUpload}.
 * @author Otávio Scherer Garcia
 * @since 4.0.0
 */
public class ServletFileUploadProducer {

	/**
	 * Creates a new instance for {@link ServletFileUpload}. You can override this method to
	 * configure if you want.
	 */
	@Dependent
	@Produces
	public ServletFileUpload getServletFileUpload() {
		ServletFileUpload uploader = new ServletFileUpload();
		uploader.setFileSizeMax(getFileSizeMax());
		uploader.setSizeMax(getSizeMax());
		
		return uploader;
	}

	/**
	 * Returns the max size permited for all uploaded files.
	 */
	protected long getSizeMax() {
		return 5 * 1024 * 1024;
	}

	/**
	 * Returns the max size permited for each uploaded file.
	 */
	protected long getFileSizeMax() {
		return 2 * 1024 * 1024;
	}
}
