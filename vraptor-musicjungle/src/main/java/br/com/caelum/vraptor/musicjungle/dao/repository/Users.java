package br.com.caelum.vraptor.musicjungle.dao.repository;

import java.util.List;

import br.com.caelum.vraptor.musicjungle.model.User;

public interface Users {
	public User register(User user);
	public User load(User user);
	public User validateCredentials(String login, String password);
	public User refresh(User user);
	public List<User> listAll();
}
