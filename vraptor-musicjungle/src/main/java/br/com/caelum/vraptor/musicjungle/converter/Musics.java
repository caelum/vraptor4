package br.com.caelum.vraptor.musicjungle.converter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.io.IOUtils;

import br.com.caelum.vraptor.interceptor.multipart.UploadedFile;
import br.com.caelum.vraptor.ioc.Component;
import br.com.caelum.vraptor.musicjungle.model.Music;

@Component
public class Musics {
	
	private File DEFAULT_FOLDER = new File("/tmp/uploads/");
	
	public Musics(){
		//creates the directory if not exists
		if(!DEFAULT_FOLDER.exists())
			DEFAULT_FOLDER.mkdirs();
	}
	
	public void save(UploadedFile file, Music music) {
		
		if(file == null)
			return;

		File path = new File(DEFAULT_FOLDER, getFileName(music));
		try {
			IOUtils.copyLarge(file.getFile(), new FileOutputStream(path));
		} catch (IOException e) {
			throw new RuntimeException("Can't write music file.", e);
		}
	}

	protected String getFileName(Music music) {
		return "Music_" + music.getId() + ".mp3";
	}
	
	public File getFile(Music music) {
		return new File(DEFAULT_FOLDER, getFileName(music));
	}

}
