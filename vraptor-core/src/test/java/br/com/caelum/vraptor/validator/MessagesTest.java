package br.com.caelum.vraptor.validator;

import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.util.test.MockValidator;

public class MessagesTest {

	private Messages messages;
	private MockValidator validator;
	
	@Before
	public void setUp() {
		validator = new MockValidator();
		messages = new Messages(validator);
	}
	
	@Test(expected=IllegalStateException.class)
	public void shouldThrowExceptionIfMessagesHasUnhandledErrors() {
		messages.add(new SimpleMessage("Test", "Test message"));
		messages.assertAbsenceOfErrors();
	}

	@Test
	public void shouldNotThrowExceptionIfMessagesHasNoUnhandledErrors() {
		messages.add(new SimpleMessage("Test", "Test message"));
		messages.handleErrors();
		messages.assertAbsenceOfErrors();
	}

	@Test
	public void shouldNotThrowExceptionIfMessagesHasNoErrors() {
		messages.assertAbsenceOfErrors();
	}
	
}
