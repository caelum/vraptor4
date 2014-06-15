package br.com.caelum.vraptor.musicjungle.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.musicjungle.dao.MusicDao;
import br.com.caelum.vraptor.musicjungle.enums.MusicType;
import br.com.caelum.vraptor.musicjungle.files.Musics;
import br.com.caelum.vraptor.musicjungle.interceptor.UserInfo;
import br.com.caelum.vraptor.musicjungle.model.Music;
import br.com.caelum.vraptor.musicjungle.model.MusicOwner;
import br.com.caelum.vraptor.musicjungle.model.User;
import br.com.caelum.vraptor.observer.upload.UploadedFile;
import br.com.caelum.vraptor.util.test.MockResult;
import br.com.caelum.vraptor.util.test.MockValidator;

public class MusicControllerTest {

	private MockResult result;
	private MockValidator validator;
	private UserInfo userInfo;
	private MusicDao dao;
	private Musics musics;
	private MusicController controller;
	
	private Music music;
	private User user;

	private UploadedFile uploadFile;
	
	@Before
	public void setUp() {
		result = new MockResult();
		validator = new MockValidator();
		userInfo = mock(UserInfo.class);
		dao = mock(MusicDao.class);
		musics = mock(Musics.class);
		controller = new MusicController(dao, userInfo, result, validator, musics);
		
		uploadFile = mock(UploadedFile.class);
		
		music = createMusic();
		user = createUser();
	}
	
	@Test
	public void shouldAddMusic() {
		when(userInfo.getUser()).thenReturn(user);

		controller.add(music, uploadFile);
		
		verify(dao).add(music);
		verify(dao).add(any(MusicOwner.class));
		verify(musics).save(uploadFile, music);

		assertEquals(music.getTitle() + " music added", result.included().get("notice"));
	}

	@Test
	public void shouldShowMusicWhenExists() {
		when(dao.load(music)).thenReturn(music);
		
		controller.show(music);
		
		assertNotNull(result.included().get("music"));
		assertEquals(music, result.included().get("music"));
	}
	
	@Test
	public void shouldNotShowMusicWhenDoesNotExists() {
		when(dao.load(music)).thenReturn(null);
		
		controller.show(music);
		
		assertNull(result.included().get("music"));
	}
	
	@Test
	public void shouldReturnMusicList() {
		when(dao.searchSimilarTitle(music.getTitle())).thenReturn(Arrays.asList(music));
		controller.search(music);
		assertEquals(Arrays.asList(music), result.included().get("musics"));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void shouldReturnEmptyList() {
		when(dao.searchSimilarTitle(music.getTitle())).thenReturn(new ArrayList<Music>());
		controller.search(music);
		List<Music> musics = (List<Music>) result.included().get("musics");
		Assert.assertTrue(musics.isEmpty());
	}
	
	@Test
	public void shouldNotDownloadMusicWhenDoesNotExist() {
		when(dao.load(music)).thenReturn(music);
		when(musics.getFile(music)).thenReturn(new File("/tmp/uploads/Music_" + music.getId() + ".mp3"));
		try {
			controller.download(music);
		} catch (FileNotFoundException e) {
			verify(musics).getFile(music);
		}
	}
	
	private Music createMusic() {
		Music music = new Music();
		music.setId(1L);
		music.setType(MusicType.ROCK);
		music.setTitle("Some Music");
		music.setDescription("Some description");
		
		return music;
	}
	
	private User createUser() {
		User user = new User();
		user.setLogin("renanigt");
		user.setName("Renan");
		user.setPassword("1234");

		return user;
	}
	
}
