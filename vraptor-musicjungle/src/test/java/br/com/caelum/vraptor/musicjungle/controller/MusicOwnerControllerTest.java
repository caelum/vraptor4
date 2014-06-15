package br.com.caelum.vraptor.musicjungle.controller;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.com.caelum.vraptor.musicjungle.dao.MusicDao;
import br.com.caelum.vraptor.musicjungle.dao.UserDao;
import br.com.caelum.vraptor.musicjungle.enums.MusicType;
import br.com.caelum.vraptor.musicjungle.interceptor.UserInfo;
import br.com.caelum.vraptor.musicjungle.model.Music;
import br.com.caelum.vraptor.musicjungle.model.MusicOwner;
import br.com.caelum.vraptor.musicjungle.model.User;
import br.com.caelum.vraptor.util.test.MockResult;
import br.com.caelum.vraptor.util.test.MockValidator;
import br.com.caelum.vraptor.validator.ValidationException;

public class MusicOwnerControllerTest {

	private MockResult result;
	private MockValidator validator;
	@Mock
	private UserInfo userInfo;
	@Mock
	private MusicDao musicDao;
	@Mock
	private UserDao userDao;
	private MusicOwnerController controller;
	
	private Music music;
	private User user;
	private User anotherUser;
	
	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		result = new MockResult();
		validator = new MockValidator();
		controller = new MusicOwnerController(musicDao, userDao, userInfo, result, validator);
		
		music = createMusic();
		user = createUser();
		anotherUser = createAnotherUser();
	}

	@Test
	public void shouldAddToMyList() {
		when(userInfo.getUser()).thenReturn(anotherUser);
		controller.addToMyList(anotherUser, music);
		verify(musicDao).add(new MusicOwner(anotherUser, music));
	}
	
	@Test(expected=ValidationException.class)
	public void shouldNotAddToMyListWhenUserNotEqualsSessionUser() {
		when(userInfo.getUser()).thenReturn(anotherUser);
		controller.addToMyList(user, music);
	}

	@Test(expected=ValidationException.class)
	public void shouldNotAddToMyListWhenUserAlreadyHaveTheMusic() {
		when(userInfo.getUser()).thenReturn(user);
		controller.addToMyList(user, music);
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
		user.setMusicOwners(createMusicOwners());

		return user;
	}

	private User createAnotherUser() {
		User user = new User();
		user.setLogin("fulano");
		user.setName("Fulano");
		user.setPassword("3456");
		
		return user;
	}
	
	private Set<MusicOwner> createMusicOwners() {
		Set<MusicOwner> musicOwners = new HashSet<MusicOwner>();
		MusicOwner musicOwner = new MusicOwner(createAnotherUser(), createMusic());
		
		musicOwners.add(musicOwner);
		
		return musicOwners;
	}
	
}
