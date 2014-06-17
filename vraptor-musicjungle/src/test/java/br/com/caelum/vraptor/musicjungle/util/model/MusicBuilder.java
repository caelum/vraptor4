package br.com.caelum.vraptor.musicjungle.util.model;

import java.util.Set;

import br.com.caelum.vraptor.musicjungle.enums.MusicType;
import br.com.caelum.vraptor.musicjungle.model.Music;
import br.com.caelum.vraptor.musicjungle.model.MusicOwner;

public class MusicBuilder extends Music {

	public MusicBuilder withId(Long id) {
		setId(id);
		return this;
	}
	
	public MusicBuilder withTitle(String title) {
		setTitle(title);
		return this;
	}

	public MusicBuilder withDescription(String description) {
		setDescription(description);
		return this;
	}
	
	public MusicBuilder withMusicOwners(Set<MusicOwner> musicOwners) {
		setMusicOwners(musicOwners);
		return this;
	}
	
	public MusicBuilder withType(MusicType type) {
		setType(type);
		return this;
	}
	
}
