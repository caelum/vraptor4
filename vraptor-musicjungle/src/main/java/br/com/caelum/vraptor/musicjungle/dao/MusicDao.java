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

import br.com.caelum.vraptor.musicjungle.model.Music;

/**
 * Data Access Object for the Music entity.
 *
 * @author Lucas Cavalcanti
 * @author Rodrigo Turini
 */
public interface MusicDao {

	/**
	 * Add a new music to the database.
	 */
	void add(Music music);

	/**
	 * Load the music from database
	 */
	Music load(Music music);

	/**
	 * Returns a list of Musics containing the specified title.
	 *
	 * @param title title to search for.
	 * @return Music list.
	 */
	List<Music> searchSimilarTitle(String title);

	/**
	 * @return List of all musics
	 */
	List<Music> listAll();

}
