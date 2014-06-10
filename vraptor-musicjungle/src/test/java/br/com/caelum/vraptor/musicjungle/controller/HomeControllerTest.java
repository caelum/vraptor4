package br.com.caelum.vraptor.musicjungle.controller;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.musicjungle.dao.UserDao;
import br.com.caelum.vraptor.musicjungle.interceptor.UserInfo;
import br.com.caelum.vraptor.musicjungle.model.User;
import br.com.caelum.vraptor.util.test.MockResult;
import br.com.caelum.vraptor.util.test.MockValidator;
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
		userInfo = new UserInfo();
		controller = new HomeController(dao, userInfo, result, validator);
		
		user = createUser();
	}
	
	@Test
	public void shouldLoginWhenUserExist() {
		when(dao.find(user.getLogin(), user.getPassword())).thenReturn(user);
		
		controller.login(user.getLogin(), user.getPassword());
		
		Assert.assertEquals(user, userInfo.getUser());
		Assert.assertFalse(validator.hasErrors());
	}

	@Test(expected=ValidationException.class)
	public void shouldNotLoginWhenUserDoesNotExist() {
		when(dao.find(user.getLogin(), user.getPassword())).thenReturn(null);
		
		controller.login(user.getLogin(), user.getPassword());
	}
	
	public User createUser() {
		User user = new User();
		user.setLogin("renanigt");
		user.setName("Renan");
		user.setPassword("1234");

		return user;
	}
}
