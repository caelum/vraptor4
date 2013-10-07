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
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;

/**
 * User entity representing the User table from the database. 
 * A persisted object of this class represents a record in the 
 * database.<br> It's annotated with <code>@Component</code> 
 * and <code>@SessionScoped</code>, thus its instances can be 
 * injected to other classes who depend on Users.
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

	// user to music mapping,
	@OneToMany(mappedBy="owner")
	private Set<MusicOwner> musicOwners;

	public Set<MusicOwner> getMusicOwners() {
		if (musicOwners == null) {
			musicOwners = new HashSet<MusicOwner>();
		}
		return musicOwners;
	}

	public void setMusicOwners(Set<MusicOwner> musicOwners) {
		this.musicOwners = musicOwners;
	}

	public Set<Music> getMusics() {
		return new HashSet<Music>(Collections2.transform(getMusicOwners(), new Function<MusicOwner, Music>() {
			@Override
			public Music apply(MusicOwner copy) {
				return copy.getMusic();
			}
		}));
	}

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

}
