package br.com.caelum.vraptor.interceptor.multipart;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;

public class MockFileItemIterator
	implements FileItemIterator {
	
	private Iterator<FileItemStream> items;
	
	public MockFileItemIterator(List<FileItemStream> items) {
		this.items = items.iterator();
	}

	@Override
	public boolean hasNext()
		throws FileUploadException, IOException {
		return items.hasNext();
	}

	@Override
	public FileItemStream next()
		throws FileUploadException, IOException {
		return items.next();
	}
}
