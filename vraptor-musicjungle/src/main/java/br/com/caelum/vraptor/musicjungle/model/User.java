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
package br.com.caelum.vraptor.musicjungle.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;

/**
 * User entity representing the User table from the database. 
 * A persisted object of this class represents a record in the 
 * database.
 */
@Entity
public class User implements Serializable {

	private static final long serialVersionUID = 4548298563023480676L;

	@Id
	@NotNull
	@Length(min = 3, max = 20)
	@Pattern(regexp = "[a-z0-9_]+", message = "{invalid_login}")
	private String login;

	@NotNull
	@Length(min = 6, max = 20)
	private String password;

	@NotNull
	@Length(min = 3, max = 100)
	private String name;

	@ManyToMany
	private Set<Music> musics = new HashSet<>();

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Set<Music> getMusics() {
		return musics;
	}

	public void setMusics(Set<Music> musics) {
		this.musics = musics;
	}

	public void add(Music music) {
		getMusics().add(music);
		music.getOwners().add(this);
	}
}
