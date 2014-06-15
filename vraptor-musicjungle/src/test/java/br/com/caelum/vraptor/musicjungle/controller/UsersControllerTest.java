package br.com.caelum.vraptor.musicjungle.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.com.caelum.vraptor.musicjungle.dao.UserDao;
import br.com.caelum.vraptor.musicjungle.enums.MusicType;
import br.com.caelum.vraptor.musicjungle.model.User;
import br.com.caelum.vraptor.util.test.MockResult;
import br.com.caelum.vraptor.util.test.MockValidator;

public class UsersControllerTest {

	private MockResult result;
	private MockValidator validator;
	@Mock
	private UserDao userDao;

	private UsersController controller;
	
	private User user;
	private User anotherUser;
	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		result = new MockResult();
		validator = new MockValidator();
		controller = new UsersController(userDao, result, validator);
		
		user = createUser();
		anotherUser = createAnotherUser();
	}

	@Test
	public void shouldOpenHomeWithMusicTypes() {
		controller.home();
		MusicType[] musicsType = (MusicType[]) result.included().get("musicTypes");
		assertThat(Arrays.asList(musicsType), hasSize(MusicType.values().length));
	}
	
	@Test
	public void shouldListAllUsers() {
		when(userDao.listAll()).thenReturn(Arrays.asList(user, anotherUser));
		controller.list();
		assertEquals(Arrays.asList(user, anotherUser), result.included().get("users"));
	}
	
	@Test
	public void shouldAddUser() {
		controller.add(user);
		verify(userDao).add(user);
		assertEquals("User " + user.getName() + " successfully added", result.included().get("notice"));
	}
	
	@Test
	public void shouldShowUser() {
		when(userDao.find(user.getLogin())).thenReturn(user);
		controller.show(user);
		assertEquals(user, result.included().get("user"));
	}
	
	private User createUser() {
		User user = new User();
		user.setLogin("renanigt");
		user.setName("Renan");
		user.setPassword("1234");

		return user;
	}

	private User createAnotherUser() {
		User user = new User();
		user.setLogin("fulano");
		user.setName("Fulano");
		user.setPassword("3456");
		
		return user;
	}
	
}
