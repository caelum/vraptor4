package br.com.caelum.vraptor.musicjungle.controller;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.com.caelum.vraptor.musicjungle.dao.UserDao;
import br.com.caelum.vraptor.musicjungle.interceptor.UserInfo;
import br.com.caelum.vraptor.musicjungle.model.User;
import br.com.caelum.vraptor.musicjungle.util.model.UserBuilder;
import br.com.caelum.vraptor.util.test.MockResult;
import br.com.caelum.vraptor.util.test.MockValidator;
import br.com.caelum.vraptor.validator.Message;
import br.com.caelum.vraptor.validator.SimpleMessage;
import br.com.caelum.vraptor.validator.ValidationException;

public class HomeControllerTest {

	private MockResult result;
	private MockValidator validator;
	private HomeController controller;
	@Mock
	private UserDao dao;
	@Mock
	private UserInfo userInfo;
	
	private User user;
	
	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		result = new MockResult();
		validator = new MockValidator();
		controller = new HomeController(dao, userInfo, result, validator);
		
		user = new UserBuilder().withName("Renan").withLogin("renanigt").withPassword("1234").build();
	}
	
	@Test
	public void shouldLoginWhenUserExists() {
		when(dao.find(user.getLogin(), user.getPassword())).thenReturn(user);
		
		controller.login(user.getLogin(), user.getPassword());
		
		assertThat(validator.getErrors(), empty());
	}

	@Test(expected=ValidationException.class)
	public void shouldNotLoginWhenUserDoesNotExists() {
		when(dao.find(user.getLogin(), user.getPassword())).thenReturn(null);
		
		controller.login(user.getLogin(), user.getPassword());
	}

	@Test
	public void shouldNotLoginWhenUserDoesNotExistsAndHasOneError() {
		when(dao.find(user.getLogin(), user.getPassword())).thenReturn(null);
		
		try {
			controller.login(user.getLogin(), user.getPassword());
			fail("Should throw an exception.");
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
	
}
