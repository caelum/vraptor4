package br.com.caelum.vraptor.musicjungle.dao.repository;

import java.util.List;

import br.com.caelum.vraptor.musicjungle.model.User;

public interface IUserRepository {
	public User add(User user);
	public User load(User user);
	public User find(String login, String password);
	public User refresh(User user);
	public List<User> listAll();
}
