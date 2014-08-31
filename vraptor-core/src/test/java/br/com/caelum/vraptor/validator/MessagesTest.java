package br.com.caelum.vraptor.validator;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

import java.util.Collection;

import org.junit.Before;
import org.junit.Test;

public class MessagesTest {

	private Messages messages;
	
	@Before
	public void setUp() {
		messages = new Messages();
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
	public void shouldNotThrowExceptionIfMessagesHasNoErrorsAndHasOtherSeverityMessages() {
		messages.add(new SimpleMessage("Test", "Test warn message", Severity.WARN));
		messages.add(new SimpleMessage("Test", "Test info message", Severity.INFO));
		messages.add(new SimpleMessage("Test", "Test success message", Severity.SUCCESS));
		messages.assertAbsenceOfErrors();
	}

	@Test
	public void shouldNotThrowExceptionIfMessagesHasNoErrors() {
		messages.assertAbsenceOfErrors();
	}

	@Test
	public void shouldGroupMessagesFromSameCategory() {
		messages.add(new SimpleMessage("client.name", "not null"));
		messages.add(new SimpleMessage("client.name", "not empty"));
		messages.add(new SimpleMessage("client.email", "is not valid e-mail"));

		Collection<String> errors = ((MessageList) messages.getErrors()).from("client.name");
		assertThat(errors, hasSize(2));
		assertThat(errors, hasItem("not null"));
		assertThat(errors, hasItem("not empty"));
		assertThat(errors, not(hasItem("is not valid e-mail")));
	}

	@Test
	public void shouldDisplayMessagesJoiningWithCommas() {
		messages.add(new SimpleMessage("client.name", "not null"));
		messages.add(new SimpleMessage("client.name", "not empty"));

		Collection<String> errors = ((MessageList) messages.getErrors()).from("client.name");
		assertThat(errors.toString(), equalTo("not null, not empty"));
	}
}
