package br.com.caelum.vraptor.musicjungle.util.model;

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
	
	public User build() {
		return user;
	}
}
