package br.com.caelum.vraptor.interceptor.multipart;

import java.util.Collections;
import java.util.List;

import javax.enterprise.inject.Alternative;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

@Alternative
public class MockFilesCommonsFileUpload
    implements ServletFileUploadCreator {

    public ServletFileUpload create(FileItemFactory fileItemFactory) {
        return new ServletFileUpload(fileItemFactory) {

            @Override
			public List<FileItem> parseRequest(HttpServletRequest request)
                throws FileUploadException {
                return Collections.emptyList();
            }
        };
    }
}
