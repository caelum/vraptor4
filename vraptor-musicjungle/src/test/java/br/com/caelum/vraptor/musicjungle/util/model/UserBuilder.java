package br.com.caelum.vraptor.musicjungle.util.model;

import java.util.Set;

import br.com.caelum.vraptor.musicjungle.model.MusicOwner;
import br.com.caelum.vraptor.musicjungle.model.User;

public class UserBuilder {

	private final User user = new User();
	
	public UserBuilder withLogin(String login) {
		user.setLogin(login);
		return this;
	}
	
	public UserBuilder withPassword(String password) {
		user.setPassword(password);
		return this;
	}

	public UserBuilder withName(String name) {
		user.setName(name);
		return this;
	}
	
	public UserBuilder withMusicOwners(Set<MusicOwner> musicOwners) {
		user.setMusicOwners(musicOwners);
		return this;
	}
	
	public User build() {
		return user;
	}
}
