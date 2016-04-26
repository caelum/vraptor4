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

import br.com.caelum.vraptor.musicjungle.model.Music;

/**
 * Default implementation for MusicDao.
 *
 * @author Lucas Cavalcanti
 * @author Rodrigo Turini
 */
public class DefaultMusicDao implements MusicDao {

	private final EntityManager entityManager;

	/**
	 * @deprecated CDI eyes only
	 */
	protected DefaultMusicDao() {
		this(null);
	}
	
	/**
	 * Creates a new MusicDao.
	 *
	 * @param entityManager JPA's EntityManager.
	 */
	@Inject
	public DefaultMusicDao(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	@Override
	public void add(Music music) {
		entityManager.persist(music);
	}

	@Override
	public List<Music> searchSimilarTitle(String title) {
		return entityManager
				.createQuery("select m from Music m where lower(m.title) like lower(:title)", Music.class)
				.setParameter("title", "%" + title+ "%")
				.getResultList();
	}
	
	@Override
	public Music load(Music music) {
		return (Music) entityManager.find(Music.class, music.getId());
	}
	
	@Override
	public List<Music> listAll() {
		return entityManager.createQuery("select m from Music m", Music.class).getResultList();
	}

}
