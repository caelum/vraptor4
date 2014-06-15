package br.com.caelum.vraptor.musicjungle.controller;

import java.util.Arrays;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.com.caelum.vraptor.musicjungle.dao.UserDao;
import br.com.caelum.vraptor.musicjungle.enums.MusicType;
import br.com.caelum.vraptor.util.test.MockResult;
import br.com.caelum.vraptor.util.test.MockValidator;

public class UsersControllerTest {

	private MockResult result;
	private MockValidator validator;
	@Mock
	private UserDao userDao;

	private UsersController controller;
	
	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		result = new MockResult();
		validator = new MockValidator();
		controller = new UsersController(userDao, result, validator);
	}

	@Test
	public void shouldOpenHomeWithMusicTypes() {
		controller.home();
		MusicType[] musicsType = (MusicType[]) result.included().get("musicTypes");
		
		assertThat(Arrays.asList(musicsType), hasSize(MusicType.values().length));
	}
	
}
