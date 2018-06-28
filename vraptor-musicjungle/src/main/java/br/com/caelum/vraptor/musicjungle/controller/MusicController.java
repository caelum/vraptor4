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

import static br.com.caelum.vraptor.view.Results.http;
import static br.com.caelum.vraptor.view.Results.json;
import static br.com.caelum.vraptor.view.Results.representation;
import static br.com.caelum.vraptor.view.Results.xml;

import java.io.File;
import java.io.FileNotFoundException;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.caelum.vraptor.Controller;
import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Path;
import br.com.caelum.vraptor.Post;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.musicjungle.dao.MusicDao;
import br.com.caelum.vraptor.musicjungle.dao.UserDao;
import br.com.caelum.vraptor.musicjungle.files.Musics;
import br.com.caelum.vraptor.musicjungle.interceptor.Public;
import br.com.caelum.vraptor.musicjungle.interceptor.UserInfo;
import br.com.caelum.vraptor.musicjungle.model.Music;
import br.com.caelum.vraptor.musicjungle.model.User;
import br.com.caelum.vraptor.observer.download.Download;
import br.com.caelum.vraptor.observer.download.FileDownload;
import br.com.caelum.vraptor.observer.upload.UploadedFile;
import br.com.caelum.vraptor.validator.Validator;

import com.google.common.base.Objects;

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
@Controller
public class MusicController {

	private static final Logger logger = LoggerFactory.getLogger(MusicController.class);

	private final Result result;
	private final Validator validator;
	private final UserInfo userInfo;
	private final MusicDao musicDao;
	private final Musics musics;
	private final UserDao userDao;

	/**
	 * @deprecated CDI eyes only
	 */
	protected MusicController() {
		this(null, null, null, null, null, null);
	}


	/**
	 * Receives dependencies through the constructor.
	 * 
	 * @param userInfo info on the logged user.
	 * @param result VRaptor result handler.
	 * @param validator VRaptor validator.
	 * @param factory dao factory.
	 * @param userDao
	 */
	@Inject
	public MusicController(MusicDao musicDao, UserInfo userInfo, 
				Result result, Validator validator, Musics musics, UserDao userDao) {
		this.musicDao = musicDao;
		this.result = result;
        this.validator = validator;
        this.userInfo = userInfo;
		this.musics = musics;
		this.userDao = userDao;
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
	public void add(final @NotNull @Valid Music music, UploadedFile file) {
		validator.onErrorForwardTo(UsersController.class).home();

		musicDao.add(music);
		
		User currentUser = userInfo.getUser();
		userDao.refresh(currentUser);
		
		currentUser.add(music);
		
		// is there a file?
		if (file != null) {
		    // Let's save the file
			musics.save(file, music);
			logger.info("Uploaded file: {}", file.getFileName());
		}

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
	    result.include("music", musicDao.load(music));
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
	@Get("/musics/search")
	public void search(Music music) {
		String title = MoreObjects.firstNonNull(music.getTitle(), "");
        result.include("musics", this.musicDao.searchSimilarTitle(title));
    }
	
	@Path("/musics/download/{m.id}")
	@Get
	public Download download(Music m) throws FileNotFoundException {
		Music music = musicDao.load(m);
		File file = musics.getFile(music);
		String contentType = "audio/mpeg";
		String filename = music.getTitle() + ".mp3";

		return new FileDownload(file, contentType, filename);
	}
	
	/**
	 * Show all list of registered musics in json format
	 */
	@Public @Path("/musics/list/json")
	public void showAllMusicsAsJSON() {
		result.use(json()).from(musicDao.listAll()).serialize();
	}

	/**
	 * Show all list of registered musics in xml format
	 */
	@Public @Path("/musics/list/xml")
	public void showAllMusicsAsXML() {
		result.use(xml()).from(musicDao.listAll()).serialize();
	}
	
	/**
	 * Show all list of registered musics in http format
	 */
	@Public @Path("/musics/list/http")
	public void showAllMusicsAsHTTP() {
		result.use(http()).body("<p class=\"content\">"+
			musicDao.listAll().toString()+"</p>");
	}

	@Public @Path("/musics/list/form")
	public void listForm() {}
	
	@Public @Path("musics/listAs")
	public void listAs() {
		result.use(representation())
			.from(musicDao.listAll()).serialize();
	}
}
