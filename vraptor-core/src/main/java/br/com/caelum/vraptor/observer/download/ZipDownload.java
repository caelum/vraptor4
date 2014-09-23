package br.com.caelum.vraptor.observer.download;

import static java.nio.file.Files.copy;
import static java.util.Arrays.asList;

import java.io.IOException;
import java.nio.file.Path;
import java.util.zip.CRC32;
import java.util.zip.CheckedOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletResponse;

/**
 * Supports multiple files download as a zip file.
 * 
 * @author Otávio Scherer Garcia
 * @since 4.1
 */
public class ZipDownload implements Download {

	private final String filename;
	private final Iterable<Path> files;

	public ZipDownload(String filename, Iterable<Path> files) {
		this.filename = filename;
		this.files = files;
	}

	public ZipDownload(String filename, Path... files) {
		this(filename, asList(files));
	}

	@Override
	public void write(HttpServletResponse response)
		throws IOException {
		response.setHeader("Content-disposition", "attachment; filename=" + filename);
		response.setHeader("Content-type", "application/zip");

		CheckedOutputStream stream = new CheckedOutputStream(response.getOutputStream(), new CRC32());
		try (ZipOutputStream zip = new ZipOutputStream(stream)) {
			for (Path file : files) {
				zip.putNextEntry(new ZipEntry(file.getFileName().toString()));
				copy(file, zip);
				zip.closeEntry();
			}
		}
	}
}
