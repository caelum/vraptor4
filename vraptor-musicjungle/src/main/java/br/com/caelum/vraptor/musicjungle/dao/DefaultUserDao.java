/***
 * Copyright (c) 2009 Caelum - www.caelum.com.br/opensource
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package br.com.caelum.vraptor.musicjungle.dao;

import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import org.hibernate.Session;

import br.com.caelum.vraptor.musicjungle.model.User;

/**
 * Default implementation for UserDao
 *
 * @author Lucas Cavalcanti
 * @author Rodrigo Turini
 */
public class DefaultUserDao implements UserDao {

	private final EntityManager entityManager;

	/**
	 * @deprecated CDI eyes only
	 */
	protected DefaultUserDao() {
		this(null);
	}

	/**
	 * Creates a new UserDao. You can receive dependencies 
	 * through constructor adding CDI's \@Inject annotation.
	 * 
	 * @param entityManager JPA's EntityManager.
	 */
	@Inject
	public DefaultUserDao(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	@Override
	public User find(String login, String password) {
		try {
			return entityManager
					.createQuery("select u from User u where u.login = :login and u.password = :password", User.class)
					.setParameter("login", login)
					.setParameter("password", password)
					.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	@Override
	public User find(String login) {
		return entityManager.find(User.class, login);
	}

	@Override
	public List<User> listAll() {
		return entityManager.createQuery("select u from User u", User.class).getResultList();
	}
	
	@Override
	public boolean containsUserWithLogin(String login) {
		Long count = entityManager
				.createQuery("select count(u) from User u where u.login = :login", Long.class)
				.setParameter("login", login)
				.getSingleResult();
		return count > 0;
	}
	
	@Override
	public void add(User user) {
		entityManager.persist(user);
	}

	@Override
	public void refresh(User user) {
		getSession().refresh(user); // You still can use Hibernate Session
	}

	@Override
	public void update(User user) {
		entityManager.merge(user);
	}
	
	private Session getSession() {
		return entityManager.unwrap(Session.class);
	}
}
