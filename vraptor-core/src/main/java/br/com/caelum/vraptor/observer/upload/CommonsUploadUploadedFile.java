package br.com.caelum.vraptor.observer.upload;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Throwables.propagateIfPossible;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.CopyOption;
import java.nio.file.Path;

import javax.enterprise.inject.Vetoed;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.io.FilenameUtils;

/**
 * An implementation for {@link UploadedFile} that delegates all operations to commons-fileupload API.
 * 
 * @author Otávio Scherer Garcia
 * @since 4.2
 */
@Vetoed
public class CommonsUploadUploadedFile implements UploadedFile {

	private final FileItem delegate;

	public CommonsUploadUploadedFile(FileItem fileItem) {
		this.delegate = fileItem;
	}

	@Override
	public String getContentType() {
		return delegate.getContentType();
	}

	@Override
	public InputStream getFile() throws IOException {
		return delegate.getInputStream();
	}

	@Override
	public String getFileName() {
		return FilenameUtils.getName(delegate.getName());
	}

	@Override
	public long getSize() {
		return delegate.getSize();
	}

	@Override
	public void writeTo(File target) throws IOException {
		checkNotNull(target, "Target can't be null");

		try {
			delegate.write(target);
		} catch (IOException e) {
			throw e;
		} catch (Exception e) {
			propagateIfPossible(e);
			throw new IOException(e);
		}
	}

	@Override
	public void writeTo(Path target, CopyOption... options) throws IOException {
		checkNotNull(target, "Target can't be null");
		writeTo(target.toFile());
	}

	@Override
	public String toString() {
		return String.format("UploadedFile[name=%s]", getFileName());
	}
}