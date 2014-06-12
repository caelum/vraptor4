package br.com.caelum.vraptor.musicjungle.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.musicjungle.dao.UserDao;
import br.com.caelum.vraptor.musicjungle.interceptor.UserInfo;
import br.com.caelum.vraptor.musicjungle.model.User;
import br.com.caelum.vraptor.util.test.MockResult;
import br.com.caelum.vraptor.util.test.MockValidator;
import br.com.caelum.vraptor.validator.Message;
import br.com.caelum.vraptor.validator.SimpleMessage;
import br.com.caelum.vraptor.validator.ValidationException;

public class HomeControllerTest {

	private MockResult result;
	private MockValidator validator;
	private HomeController controller;
	private UserDao dao;
	private UserInfo userInfo;
	
	private User user;
	
	@Before
	public void setUp() {
		result = new MockResult();
		validator = new MockValidator();
		dao = mock(UserDao.class);
		userInfo = mock(UserInfo.class);
		controller = new HomeController(dao, userInfo, result, validator);
		
		user = createUser();
	}
	
	@Test
	public void shouldLoginWhenUserExist() {
		when(dao.find(user.getLogin(), user.getPassword())).thenReturn(user);
		
		controller.login(user.getLogin(), user.getPassword());
		
		assertFalse(validator.hasErrors());
	}

	@Test(expected=ValidationException.class)
	public void shouldNotLoginWhenUserDoesNotExist() {
		when(dao.find(user.getLogin(), user.getPassword())).thenReturn(null);
		
		controller.login(user.getLogin(), user.getPassword());
	}

	@Test
	public void shouldNotLoginWhenUserDoesNotExistAndHasOneError() {
		when(dao.find(user.getLogin(), user.getPassword())).thenReturn(null);
		
		try {
			controller.login(user.getLogin(), user.getPassword());
			fail();
		} catch (ValidationException e) {
			List<Message> errors = e.getErrors();
			
			assertThat(errors, hasSize(1));
			assertTrue(errors.contains(new SimpleMessage("login", "invalid_login_or_password")));
		}
	}

	@Test
	public void shouldLogoutUser() {
		controller.logout();
		
		verify(userInfo).logout();
	}
	
	private User createUser() {
		User user = new User();
		user.setLogin("renanigt");
		user.setName("Renan");
		user.setPassword("1234");

		return user;
	}
}
