package br.com.caelum.vraptor.observer.download;

import static com.google.common.base.Objects.firstNonNull;
import static java.util.Objects.requireNonNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

import javax.enterprise.inject.Vetoed;

/**
 * A Builder to create a proper instance for {@link Download} class.
 * 
 * @author Otávio S Garcia
 * @since 4.1.0
 */
@Vetoed
public final class DownloadBuilder {

	/**
	 * Creates an instance for build a {@link FileDownload}.<br>
	 * <code>
	 *     Download download = DownloadBuilder.of(myFile)
	 *         .withFileName("resume.txt") // optional, default is file.getName()
	 *         .withContentType("text/plain") // optional, null will sent as octet/stream
	 *         .downloadable() // optional, default is inline content
	 *         .build();
	 * </code>
	 */
	public static FileDownloadBuilder of(File file) {
		return new FileDownloadBuilder(file);
	}

	/**
	 * Creates an instance for build a {@link InputStreamDownload}.<br>
	 * <code>
	 *     Download download = DownloadBuilder.of(myInputStream)
	 *         .withFileName("resume.txt") // optional
	 *         .withContentType("text/plain") // optional, null will sent as octet/stream
	 *         .downloadable() // optional, default is inline content
	 *         .withSize(100L) // optional 
	 *         .build();
	 * </code>
	 */
	public static InputStreamDownloadBuilder of(InputStream input) {
		return new InputStreamDownloadBuilder(input);
	}

	/**
	 * Creates an instance for build a {@link ByteArrayDownload}.<br>
	 * <code>
	 *     Download download = DownloadBuilder.of(myFile)
	 *         .withFileName("resume.txt") // optional
	 *         .withContentType("text/plain") // optional, null will sent as octet/stream
	 *         .downloadable() // optional, default is inline content
	 *         .build();
	 * </code>
	 */
	public static ByteArrayDownloadBuilder of(byte[] input) {
		return new ByteArrayDownloadBuilder(input);
	}

	public static class FileDownloadBuilder {
		private final File file;
		private String fileName;
		private String contentType;
		private boolean doDownload;

		public FileDownloadBuilder(File file) {
			this.file = requireNonNull(file, "File can't be null");
		}

		public FileDownloadBuilder withFileName(String fileName) {
			this.fileName = fileName;
			return this;
		}

		public FileDownloadBuilder withContentType(String contentType) {
			this.contentType = contentType;
			return this;
		}

		public FileDownloadBuilder downloadable() {
			this.doDownload = true;
			return this;
		}

		public FileDownload build()
			throws FileNotFoundException {
			fileName = firstNonNull(fileName, file.getName());
			return new FileDownload(file, contentType, fileName, doDownload);
		}
	}

	public static class InputStreamDownloadBuilder {
		private final InputStream input;
		private String fileName;
		private String contentType;
		private long size;
		private boolean doDownload;

		public InputStreamDownloadBuilder(InputStream input) {
			this.input = requireNonNull(input, "InputStream can't be null");
		}

		public InputStreamDownloadBuilder withFileName(String fileName) {
			this.fileName = fileName;
			return this;
		}

		public InputStreamDownloadBuilder withContentType(String contentType) {
			this.contentType = contentType;
			return this;
		}

		public InputStreamDownloadBuilder withSize(long size) {
			this.size = size;
			return this;
		}

		public InputStreamDownloadBuilder downloadable() {
			this.doDownload = true;
			return this;
		}

		public InputStreamDownload build() {
			return new InputStreamDownload(input, contentType, fileName, doDownload, size);
		}
	}

	public static class ByteArrayDownloadBuilder {
		private final byte[] buff;
		private String fileName;
		private String contentType;
		private boolean doDownload;

		public ByteArrayDownloadBuilder(byte[] buff) {
			this.buff = requireNonNull(buff, "byte[] can't be null");
		}

		public ByteArrayDownloadBuilder withFileName(String fileName) {
			this.fileName = fileName;
			return this;
		}

		public ByteArrayDownloadBuilder withContentType(String contentType) {
			this.contentType = contentType;
			return this;
		}

		public ByteArrayDownloadBuilder downloadable() {
			this.doDownload = true;
			return this;
		}

		public ByteArrayDownload build() {
			return new ByteArrayDownload(buff, contentType, fileName, doDownload);
		}
	}
}