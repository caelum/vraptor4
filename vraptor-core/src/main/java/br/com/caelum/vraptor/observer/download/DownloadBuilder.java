package br.com.caelum.vraptor.observer.download;

import com.thoughtworks.xstream.InitializationException;

import static com.google.common.base.MoreObjects.firstNonNull;
import static java.util.Objects.requireNonNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;

import javax.enterprise.inject.Vetoed;

/**
 * A Builder to create a proper instance for {@link Download} class.
 * 
 * @author Otávio S Garcia
 * @since 4.1.0
 */
@Vetoed
public final class DownloadBuilder {

	private DownloadBuilder () {
		throw new InitializationException("Not allowed to initialize");
	}

	/**
	 * Creates an instance for build a {@link FileDownload}.<br>
	 * 
	 * @param file The input file.
	 * @throws NullPointerException If the {@code file} argument is {@code null}
	 */
	public static FileDownloadBuilder of(File file) {
		return new FileDownloadBuilder(file);
	}

	/**
	 * Creates an instance for build a {@link InputStreamDownload}.<br>
	 * 
	 * @param input The input InputStream to process.
	 * @throws NullPointerException If the {@code input} argument is {@code null}
	 */
	public static InputStreamDownloadBuilder of(InputStream input) {
		return new InputStreamDownloadBuilder(input);
	}

	/**
	 * Creates an instance for build a {@link ByteArrayDownload}.<br>
	 * 
	 * @param input The input byte array.
	 * @throws NullPointerException If the {@code input} argument is {@code null}
	 */
	public static ByteArrayDownloadBuilder of(byte[] input) {
		return new ByteArrayDownloadBuilder(input);
	}

	/**
	 * Creates an instance for build a {@link ZipDownload}.<br>
	 * 
	 * @param files List of input files
	 * @throws NullPointerException If the {@code input} argument is {@code null}
	 */
	public static ZipDownloadBuilder of(List<Path> files) {
		return new ZipDownloadBuilder(files);
	}

	static abstract class AbstractDownloadBuilder<T> {
		protected String fileName;
		protected String contentType;
		protected boolean doDownload;

		public T withFileName(String fileName) {
			this.fileName = fileName;
			return (T) this;
		}

		public T withContentType(String contentType) {
			this.contentType = contentType;
			return (T) this;
		}

		public T downloadable() {
			this.doDownload = true;
			return (T) this;
		}
	}

	public static class FileDownloadBuilder extends AbstractDownloadBuilder<FileDownloadBuilder> {
		private final File file;

		FileDownloadBuilder(File file) {
			this.file = requireNonNull(file, "File can't be null");
		}

		public FileDownload build() throws FileNotFoundException {
			fileName = firstNonNull(fileName, file.getName());
			return new FileDownload(file, contentType, fileName, doDownload);
		}
	}

	public static class InputStreamDownloadBuilder extends AbstractDownloadBuilder<InputStreamDownloadBuilder> {
		private final InputStream input;
		private long size;

		InputStreamDownloadBuilder(InputStream input) {
			this.input = requireNonNull(input, "InputStream can't be null");
		}

		public InputStreamDownloadBuilder withSize(long size) {
			this.size = size;
			return this;
		}

		public InputStreamDownload build() {
			return new InputStreamDownload(input, contentType, fileName, doDownload, size);
		}
	}

	public static class ByteArrayDownloadBuilder extends AbstractDownloadBuilder<ByteArrayDownloadBuilder> {
		private final byte[] buff;

		ByteArrayDownloadBuilder(byte[] buff) {
			this.buff = requireNonNull(buff, "byte[] can't be null");
		}

		public ByteArrayDownload build() {
			return new ByteArrayDownload(buff, contentType, fileName, doDownload);
		}
	}

	public static class ZipDownloadBuilder extends AbstractDownloadBuilder<ZipDownloadBuilder> {
		private final List<Path> files;

		ZipDownloadBuilder(List<Path> files) {
			this.files = requireNonNull(files, "files can't be null");
		}

		public ZipDownload build() {
			return new ZipDownload(fileName, files);
		}
	}
}
