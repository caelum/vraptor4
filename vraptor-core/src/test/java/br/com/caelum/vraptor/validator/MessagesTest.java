package br.com.caelum.vraptor.validator;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isEmptyString;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.el.ELProcessor;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class MessagesTest {

	@Rule
	public ExpectedException exception = ExpectedException.none();

	private Messages messages;

	@Before
	public void setUp() {
		messages = new Messages();
	}

	@Test
	public void shouldThrowExceptionIfMessagesHasUnhandledErrors() {
		exception.expect(ValidationFailedException.class);
		exception.expectMessage(containsString("There are validation errors and you forgot to specify where to go."));

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
	public void shouldGroupMessagesInMapFromSameCategory() {
		messages.add(new SimpleMessage("client.name", "not null"));
		messages.add(new SimpleMessage("client.name", "not empty"));
		messages.add(new SimpleMessage("client.email", "not null"));

		MessageList messageList = (MessageList) messages.getErrors();

		List<Message> messagesName = new ArrayList<>(messageList.getGrouped().get("client.name"));
		assertThat(messagesName, hasSize(2));
		assertThat(messagesName, contains(messageList.get(0), messageList.get(1)));

		List<Message> messagesEmail = new ArrayList<>(messageList.getGrouped().get("client.email"));
		assertThat(messagesEmail, hasSize(1));
		assertThat(messagesEmail, contains(messageList.get(2)));
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

	@Test
	public void testReturnMessagesBySeverity() {
		Message message0 = new SimpleMessage("client.id", "not null", Severity.ERROR);
		Message message1 = new SimpleMessage("client.name", "not null", Severity.ERROR);
		Message message2 = new SimpleMessage("client.email", "is valid", Severity.INFO);
		Message message3 = new SimpleMessage("client.email", "was not checked", Severity.WARN);

		messages.add(message0);
		messages.add(message1);
		messages.add(message2);
		messages.add(message3);

		assertThat(messages.getErrors(), contains(message0, message1));
		assertThat(messages.getInfo(), contains(message2));
		assertThat(messages.getWarnings(), contains(message3));
		assertThat(messages.getSuccess(), empty());
	}

	@Test
	public void testElExpressionGettingMessagesByCaegory() {
		messages.add(new SimpleMessage("client.id", "will generated", Severity.INFO));
		messages.add(new SimpleMessage("client.name", "not null"));
		messages.add(new SimpleMessage("client.name", "not empty"));

		ELProcessor el = new ELProcessor();
		el.defineBean("messages", messages);

		String result = el.eval("messages.errors.from('client.name')").toString();
		assertThat(result, is("not null, not empty"));

		result = el.eval("messages.errors.from('client.name').join(' - ')").toString();
		assertThat(result, is("not null - not empty"));

		result = el.eval("messages.errors.from('client.id')").toString();
		assertThat(result, isEmptyString());

		result = el.eval("messages.info.from('client.id')").toString();
		assertThat(result, is("will generated"));
	}
}
