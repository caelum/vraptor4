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
import br.com.caelum.vraptor.musicjungle.util.model.MusicBuilder;
import br.com.caelum.vraptor.musicjungle.util.model.UserBuilder;
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
		
		music = new MusicBuilder().withId(1L).withType(MusicType.ROCK).withTitle("Some Music").withDescription("Some description").build();
		user = new UserBuilder().withName("Renan").withLogin("renanigt").withPassword("1234").withMusicOwners(createMusicOwners()).build();
		anotherUser = new UserBuilder().withName("Fulano").withLogin("fulano").withPassword("3456").build();
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

	private Set<MusicOwner> createMusicOwners() {
		Set<MusicOwner> musicOwners = new HashSet<MusicOwner>();
		MusicOwner musicOwner = new MusicOwner(anotherUser, music);
		
		musicOwners.add(musicOwner);
		
		return musicOwners;
	}
	
}
