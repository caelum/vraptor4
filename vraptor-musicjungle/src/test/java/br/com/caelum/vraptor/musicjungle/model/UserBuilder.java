package br.com.caelum.vraptor.musicjungle.model;

import java.util.Set;

@SuppressWarnings("serial")
public class UserBuilder extends User {

	public UserBuilder withLogin(String login) {
		setLogin(login);
		return this;
	}
	
	public UserBuilder withPassword(String password) {
		setPassword(password);
		return this;
	}

	public UserBuilder withName(String name) {
		setName(name);
		return this;
	}
	
	public UserBuilder withMusicOwners(Set<MusicOwner> musicOwners) {
		setMusicOwners(musicOwners);
		return this;
	}
	
}
