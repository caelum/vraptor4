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
import javax.validation.Valid;

import br.com.caelum.vraptor.Controller;
import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.Post;
import br.com.caelum.vraptor.Put;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.musicjungle.dao.MusicDao;
import br.com.caelum.vraptor.musicjungle.dao.UserDao;
import br.com.caelum.vraptor.musicjungle.enums.MusicType;
import br.com.caelum.vraptor.musicjungle.interceptor.Public;
import br.com.caelum.vraptor.musicjungle.interceptor.UserInfo;
import br.com.caelum.vraptor.musicjungle.model.Music;
import br.com.caelum.vraptor.musicjungle.model.User;
import br.com.caelum.vraptor.musicjungle.validation.LoginAvailable;
import br.com.caelum.vraptor.validator.SimpleMessage;
import br.com.caelum.vraptor.validator.Validator;

/**
 * The resource <code>UsersController</code> handles all user 
 * operations, such as adding new users, listing users, and so on.
 */
@Controller
public class UsersController {

	private final Validator validator;
	private final Result result;
	private final UserDao userDao;
	private final UserInfo userInfo;
	private final MusicDao musicDao;

	/**
	 * @deprecated CDI eyes only
	 */
	protected UsersController() {
		this(null, null, null, null, null);
	}

	/**
	 * Receives dependencies through the constructor.
	 * 
	 * @param factory dao factory.
	 * @param userInfo info on the logged user.
	 * @param result VRaptor result handler.
	 * @param validator VRaptor validator.
	 * @param userInfo 
	 */
	@Inject
	public UsersController(UserDao dao, Result result, Validator validator, 
			UserInfo userInfo, MusicDao musicDao) {
		
		this.userDao = dao;
		this.result = result;
		this.validator = validator;
		this.userInfo = userInfo;
		this.musicDao = musicDao;
	}

	/**
	 * Accepts HTTP GET requests.
	 * 
	 * URL:  /home
	 * View: /WEB-INF/jsp/user/home.jsp
	 *
	 * Shows user's home page containing his Music collection.
	 */
	@Get("/")
	public void home() {
	    result.include("musicTypes", MusicType.values());
	}

	/**
     * Accepts HTTP GET requests.
     * 
     * URL:  /users (only GET requests for this URL)
     * View: /WEB-INF/jsp/user/list.jsp
     *
     * Lists all users.
     */
	@Get("/users")
	public void list() {
        result.include("users", userDao.listAll());
    }

	/**
	 * Accepts HTTP POST requests.
	 * 
	 * URL:	 /users
	 * View: /WEB-INF/jsp/user/add.jsp
	 *
	 * The "user" parameter will be populated with the request 
	 * parameters, for example:
	 *
	 * POST	/user
	 * user.name=Nico
	 * user.login=555555
	 *
	 * automatically populates the name and login parameters on 
	 * the user object with values Nico and 555555.
	 *
	 * Adds new users to the database.
	 */
	@Path("/users")
	@Post
	@Public
	public void add(@Valid @LoginAvailable User user) {
        validator.onErrorUsePageOf(HomeController.class).login();
        
		userDao.add(user);

		// you can add objects to result even in redirects. Added objects will
		// survive one more request when redirecting.
		result.include("notice", "User " + user.getName() + " successfully added");
		result.redirectTo(HomeController.class).login();
	}

	/**
	 * Accepts HTTP GET requests.
	 * 
	 * URL:  /users/{login} (for example, /users/john 
	 * shows information of the user with login john)
	 * View: /WEB-INF/jsp/user/view.jsp
	 *
	 * Shows information on the specified user.
	 * @param user
	 */
	@Path("/users/{user.login}")
	@Get
	public void show(User user) {
	    result.include("user", userDao.find(user.getLogin()));

	    //You can redirect to any page if you like.
	    result.forwardTo("/WEB-INF/jsp/users/view.jsp");
	    
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
	public void addToMyList(User user, Music music) {
    	User currentUser = userInfo.getUser();
	    userDao.refresh(currentUser);
	    
	    validator.check(user.getLogin().equals(currentUser.getLogin()), 
	            new SimpleMessage("user", "you_cant_add_to_others_list"));

	    validator.check(!currentUser.getMusics().contains(music), 
	            new SimpleMessage("music", "you_already_have_this_music"));

		validator.onErrorUsePageOf(UsersController.class).home();

		music = musicDao.load(music);
		currentUser.add(music);
		
		result.redirectTo(UsersController.class).home();
	}

}
