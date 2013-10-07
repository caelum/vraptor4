package br.com.caelum.vraptor.musicjungle.files;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import br.com.caelum.vraptor.interceptor.multipart.UploadedFile;
import br.com.caelum.vraptor.musicjungle.model.Music;

public class Musics {
	
	private Path DEFAULT_FOLDER = Paths.get("/tmp/uploads/");
	
	public Musics(){
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
    		
    		try {
    		    Files.write(path, file.getContent());
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
