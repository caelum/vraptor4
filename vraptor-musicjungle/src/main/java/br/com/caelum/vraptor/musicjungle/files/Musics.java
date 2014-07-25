package br.com.caelum.vraptor.musicjungle.files;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;

import br.com.caelum.vraptor.musicjungle.model.Music;
import br.com.caelum.vraptor.observer.upload.UploadedFile;

@Dependent
public class Musics {
	
	private final Path DEFAULT_FOLDER = Paths.get("/tmp/uploads/");
	
	@PostConstruct
	public void init() {
		if(!Files.exists(DEFAULT_FOLDER)) {
			try {
				Files.createDirectories(DEFAULT_FOLDER);
			} catch (IOException e) {
				throw new IllegalStateException(e);
			}
		}
	}
	
	public void save(UploadedFile file, Music music) {
		if(file != null) {
    		Path path = DEFAULT_FOLDER.resolve(getFileName(music));
    		
    		try(InputStream in = file.getFile()) {
    		    Files.copy(in, path,StandardCopyOption.REPLACE_EXISTING);
    		} catch (IOException e) {
    		    throw new IllegalStateException(e);
    		}
		}
	}

	protected String getFileName(Music music) {
		return "Music_" + music.getId() + ".mp3";
	}
	
	public File getFile(Music music) {
		return DEFAULT_FOLDER.resolve(getFileName(music)).toFile();
	}
}
