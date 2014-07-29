package br.com.caelum.vraptor.musicjungle.controller;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.com.caelum.vraptor.musicjungle.dao.MusicDao;
import br.com.caelum.vraptor.musicjungle.dao.UserDao;
import br.com.caelum.vraptor.musicjungle.enums.MusicType;
import br.com.caelum.vraptor.musicjungle.interceptor.UserInfo;
import br.com.caelum.vraptor.musicjungle.model.User;
import br.com.caelum.vraptor.musicjungle.util.model.UserBuilder;
import br.com.caelum.vraptor.util.test.MockResult;
import br.com.caelum.vraptor.util.test.MockValidator;

public class UsersControllerTest {

	private MockResult result;
	private MockValidator validator;
	private UsersController controller;
	private User user;
	private User anotherUser;
	
	@Mock private UserDao userDao;
	@Mock private UserInfo info;
	@Mock private MusicDao musics;
	
	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		result = new MockResult();
		validator = new MockValidator();
		controller = new UsersController(userDao, result, validator, info, musics);
		user = new UserBuilder().withName("Renan").withLogin("renanigt").withPassword("1234").build();
		anotherUser = new UserBuilder().withName("Fulano").withLogin("fulano").withPassword("3456").build();
	}

	@Test
	public void shouldOpenHomeWithMusicTypes() {
		controller.home();
		MusicType[] musicsType = (MusicType[]) result.included().get("musicTypes");
		assertThat(Arrays.asList(musicsType), hasSize(MusicType.values().length));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void shouldListAllUsers() {
		when(userDao.listAll()).thenReturn(Arrays.asList(user, anotherUser));
		controller.list();
		assertThat((List<User>)result.included().get("users"), contains(user, anotherUser));
	}
	
	@Test
	public void shouldAddUser() {
		controller.add(user);
		verify(userDao).add(user);
		assertThat(result.included().get("notice").toString(), is("User " + user.getName() + " successfully added"));
	}
	
	@Test
	public void shouldShowUser() {
		when(userDao.find(user.getLogin())).thenReturn(user);
		controller.show(user);
		assertThat((User) result.included().get("user"), is(user));
	}
	
}
