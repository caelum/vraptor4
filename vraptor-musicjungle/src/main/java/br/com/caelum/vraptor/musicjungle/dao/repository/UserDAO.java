package br.com.caelum.vraptor.musicjungle.dao.repository;

import java.util.List;

import javax.persistence.criteria.Predicate;

import br.com.caelum.vraptor.musicjungle.dao.GenericDAO;
import br.com.caelum.vraptor.musicjungle.model.User;

public class UserDAO extends GenericDAO<User> implements IUserRepository {

	public UserDAO() {
		super(User.class);
	}

	@Override
	public User add(User user) {
		return persist(user);
	}
	
	@Override
	public User load(User user) {
		return findByPK(user.getLogin());
	}

	@Override
	public List<User> listAll() {
		return findAll();
	}

	@Override
	public User find(String login, String password) {
		Predicate loginEqual = builder.equal(from.<String>get("login"), login);
		Predicate passwordEqual = builder.equal(from.<String>get("password"), password);
		return findUniqueByPredicate(builder.and(loginEqual, passwordEqual));
	}

	@Override
	public User refresh(User user) {
		return super.refresh(user);
	}
}
