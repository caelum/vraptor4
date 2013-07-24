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

import static org.hibernate.criterion.Restrictions.eq;

import java.util.List;

import javax.inject.Inject;

import org.hibernate.Criteria;
import org.hibernate.Session;

import br.com.caelum.vraptor.musicjungle.model.User;

/**
 * Default implementation for UserDao
 *
 * @author Lucas Cavalcanti
 * @author Rodrigo Turini
 */
public class DefaultUserDao implements UserDao {

	@Inject
	private Session session;

	/**
	 * Creates a new UserDao. You can receive dependencies 
	 * through constructor, because this class is annotated 
	 * with <code>@Component</code>. This class can be used
	 * as dependency of another class, as well.
	 * 
	 * @param session Hibernate's Session.
	 */
//	public DefaultUserDao(Session session) {
//		this.session = session;
//	}

	@Override
	public User find(String login, String password) {
		Criteria criteria = session.createCriteria(User.class);
		criteria.add(eq("login", login)).add(eq("password", password));
		return (User) criteria.uniqueResult();
	}

	@Override
	public User find(String login) {
		Criteria criteria = findUserByLogin(login);
		return (User) criteria.uniqueResult();
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<User> listAll() {
		return session.createCriteria(User.class).list();
	}
	
	@Override
	public boolean containsUserWithLogin(String login) {
		return !findUserByLogin(login).list().isEmpty();
	}
	
	private Criteria findUserByLogin(String login) {
		Criteria criteria = session.createCriteria(User.class);
		return criteria.add(eq("login", login));
	}
	
	@Override
	public void add(User user) {
		session.save(user);
	}

	@Override
	public void refresh(User user) {
		session.refresh(user);
	}

	@Override
	public void update(User user) {
		session.update(user);
	}

}
