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

import static br.com.caelum.vraptor.musicjungle.validation.CustomMatchers.notEmpty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import org.apache.log4j.Logger;

import com.google.common.base.Objects;

import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.Post;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.Validator;
import br.com.caelum.vraptor.interceptor.multipart.UploadedFile;
import br.com.caelum.vraptor.musicjungle.dao.MusicDao;
import br.com.caelum.vraptor.musicjungle.interceptor.UserInfo;
import br.com.caelum.vraptor.musicjungle.model.Music;
import br.com.caelum.vraptor.musicjungle.model.MusicOwner;
import br.com.caelum.vraptor.validator.Validations;

/**
 * The resource <code>MusicController</code> handles all Music 
 * operations, such as adding new Musics, listing all, and so on.
 *
 * This is a RESTful Resource, so we will explain how to use REST 
 * on VRaptor 3 here.
 * 
 * POST /musics -> adds a music
 *
 * GET /musics/{id} -> shows the music of given id
 */
@Resource
public class MusicController {

	private static final Logger LOG = Logger.getLogger(MusicController.class);

    private final Result result;
    private final Validator validator;
    private final UserInfo userInfo;
	private final MusicDao dao;

	/**
	 * Receives dependencies through the constructor.
	 * 
	 * @param userInfo info on the logged user.
	 * @param result VRaptor result handler.
	 * @param validator VRaptor validator.
	 * @param factory dao factory.
	 */
	public MusicController(MusicDao dao, UserInfo userInfo, 
				Result result, Validator validator) {
		
		this.dao = dao;
		this.result = result;
        this.validator = validator;
        this.userInfo = userInfo;
	}

	/**
	 * Accepts HTTP POST requests.
	 * 
	 * URL:  /musics
	 * View: /WEB-INF/jsp/music/add.jsp
	 *
	 * The method adds a new music and updates the user.
	 * We use POST HTTP verb when we want to create some resource.
	 *
	 * The <code>UploadedFile</code> is automatically handled
	 * by VRaptor's <code>MultipartInterceptor</code>.
	 */
	@Path("/musics")
	@Post
	public void add(final Music music, UploadedFile file) {
		
	    validator.checking(new Validations() {{
	    	if (music != null) {
	    		that(music.getTitle(), is(notEmpty()), "login", "invalid_title");
	    		that(music.getType(), is(notNullValue()), "name", "invalid_type");
	    		that(music.getDescription(), is(notEmpty()), "description", "invalid_description");
	    		that(music.getDescription().length() >= 6, "description", "invalid_description");
	    	}
		}});

		validator.onErrorForwardTo(UsersController.class).home();

		// is there a file?
		if (file != null) {
		    // usually we would save the file, in this case, we just log :)
			LOG.info("Uploaded file: " + file.getFileName());
		}

		dao.add(music);
		dao.add(new MusicOwner(userInfo.getUser(), music));

		// you can add objects to result even in redirects. Added objects will
		// survive one more request when redirecting.
		result.include("notice", music.getTitle() + " music added");

		result.redirectTo(UsersController.class).home();
	}

	/**
	 * Accepts HTTP GET requests.
	 * 
	 * URL:  /musics/{id}
	 * View: /WEB-INF/jsp/music/show.jsp
	 * Shows the page with information about given Music
	 *
	 * We should only use GET HTTP verb for safe operations. For 
	 * instance, showing a Music has no side effects, so GET is fine.
	 *
	 * We can use templates for Paths, so VRaptor will automatically extract
	 * variables of the matched URI, and set the fields on parameters.
	 * 
	 * In this case, GET /musics/15 will execute the method below, and
	 * there will be a parameter music.id=15 on request, causing music.getId() 
	 * equal to 15.
	 */
	@Path("/musics/{music.id}")
	@Get
	public void show(Music music) {
	    result.include("music", dao.load(music));
	}

    /**
	 * Accepts HTTP GET requests.
	 * 
	 * URL:  /musics/search
	 * View: /WEB-INF/jsp/music/search.jsp
	 *
	 * Searches are not unique resources, so it is ok to use searches with
	 * query parameters.
	 */
	@Path("/musics/search")
	@Get
	public void search(Music music) {
		String title = Objects.firstNonNull(music.getTitle(), "");
        result.include("musics", this.dao.searchSimilarTitle(title));
    }
	
	

}
