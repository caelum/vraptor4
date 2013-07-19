package br.com.caelum.vraptor.musicjungle.acceptance.builder;

import br.com.caelum.vraptor.musicjungle.enums.MusicType;
import br.com.caelum.vraptor.musicjungle.model.Music;

public class MusicBuilder {

	private final Music music = new Music();

	public MusicBuilder(long id, String title, 
			String description, MusicType musicType) {
		
		music.setId(id);
		music.setTitle(title);
		music.setDescription(description);
		music.setType(musicType);
	}

	public Music create() {
		return this.music;
	}

}
