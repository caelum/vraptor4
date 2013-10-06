package br.com.caelum.vraptor.musicjungle.custom;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.Specializes;

import org.apache.commons.fileupload.servlet.ServletFileUpload;

import br.com.caelum.vraptor.interceptor.multipart.ServletFileUploadProducer;

public class CustomServletFileUploadProducer extends ServletFileUploadProducer {

    /**
     * Creates a new instance for {@link ServletFileUpload}. You can override this method to
     * configure if you want.
     */
    @Dependent
    @Produces
    @Specializes
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
		return 50 * 1024 * 1024;
	}

	/**
	 * Returns the max size permited for each uploaded file.
	 */
	protected long getFileSizeMax() {
		return 10 * 1024 * 1024;
	}
}
