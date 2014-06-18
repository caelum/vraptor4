package br.com.caelum.vraptor.musicjungle.util.model;

import java.util.Set;

import br.com.caelum.vraptor.musicjungle.enums.MusicType;
import br.com.caelum.vraptor.musicjungle.model.Music;
import br.com.caelum.vraptor.musicjungle.model.MusicOwner;

public class MusicBuilder {

	private final Music music = new Music();
	
	public MusicBuilder withId(Long id) {
		music.setId(id);
		return this;
	}
	
	public MusicBuilder withTitle(String title) {
		music.setTitle(title);
		return this;
	}

	public MusicBuilder withDescription(String description) {
		music.setDescription(description);
		return this;
	}
	
	public MusicBuilder withMusicOwners(Set<MusicOwner> musicOwners) {
		music.setMusicOwners(musicOwners);
		return this;
	}
	
	public MusicBuilder withType(MusicType type) {
		music.setType(type);
		return this;
	}
	
	public Music build() {
		return music;
	}
	
}
