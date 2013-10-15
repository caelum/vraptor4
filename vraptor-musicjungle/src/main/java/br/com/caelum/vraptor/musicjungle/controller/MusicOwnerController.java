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
package br.com.caelum.vraptor.musicjungle.controller;

import javax.inject.Inject;

import br.com.caelum.vraptor.Controller;
import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.Put;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.musicjungle.dao.MusicDao;
import br.com.caelum.vraptor.musicjungle.dao.UserDao;
import br.com.caelum.vraptor.musicjungle.interceptor.UserInfo;
import br.com.caelum.vraptor.musicjungle.model.Music;
import br.com.caelum.vraptor.musicjungle.model.MusicOwner;
import br.com.caelum.vraptor.musicjungle.model.User;
import br.com.caelum.vraptor.validator.SimpleMessage;
import br.com.caelum.vraptor.validator.Validator;

/**
 * The resource <code>MusicOwnerController</code> handles all 
 * MusicOwner operations, such as adding new Musics to a user list.
 */
@Controller
public class MusicOwnerController {

	private Result result;
	private Validator validator;
	private UserInfo userInfo;
	private MusicDao musicDao;
	private UserDao userDao;
	
	//CDI eyes only
	@Deprecated
	public MusicOwnerController() {
	}

	/**
	 * Receives dependencies through the constructor.
	 * 
	 * @param userDao user data access object.
	 * @param userInfo info on the logged user.
	 * @param result VRaptor result handler.
	 * @param validator VRaptor validator.
	 */
	@Inject
	public MusicOwnerController(MusicDao musicDao, UserDao userDao, 
			UserInfo userInfo, Result result, Validator validator) {
		this.musicDao = musicDao;
		this.userDao = userDao;
		this.result = result;
        this.validator = validator;
        this.userInfo = userInfo;
	}

    /**
     * Accepts HTTP PUT requests. <br>
     * 
     * <strong>URL:</strong> /users/login/musics/id (for example, 
     * /users/john/musics/3 adds the music with id 3 to the john's 
     * collection)<br>
     * 
     * <strong>View:</strong> redirects to user's home <br>
     *
     * You can use more than one variable on URI. Since the browsers 
     * don't support PUT method you have to pass an additional parameter: 
     * <strong>_method=PUT</strong> for calling this method.<br>
     *
     * This method adds a music to a user's collection.
     */
    @Path("/users/{user.login}/musics/{music.id}")
    @Put
	public void addToMyList(final User user, final Music music) {
	    final User sessionUser = refreshUser();
	    
	    validator.check(user.getLogin().equals(sessionUser.getLogin()), 
	            new SimpleMessage("user", "you_cant_add_to_others_list"));

	    validator.check(!sessionUser.getMusics().contains(music), 
	            new SimpleMessage("music", "you_already_have_this_music"));

		validator.onErrorUsePageOf(UsersController.class).home();

		musicDao.add(new MusicOwner(user, music));

		result.redirectTo(UsersController.class).home();
	}

    /*
     * Refreshes user data from database
     */
    private User refreshUser() {
        User user = userInfo.getUser();
		userDao.refresh(user);
        return user;
    }

}