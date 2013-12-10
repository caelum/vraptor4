package br.com.caelum.vraptor.converter;

import static com.google.common.base.Objects.toStringHelper;

import java.util.ResourceBundle;

import br.com.caelum.vraptor.validator.I18nMessage;
import br.com.caelum.vraptor.validator.Message;
import br.com.caelum.vraptor.validator.Severity;

import com.google.common.base.Objects;
import com.google.common.base.Strings;

public class ConversionMessage implements Message {

	private static final long serialVersionUID = 1L;
	private Message message;
	private String category;
	private Severity severity;

	public ConversionMessage(Message message, String category) {
		this.message = message;
		this.category = category;
		this.severity = Severity.ERROR;
	}

	public ConversionMessage(String key, Object... parameters) {
		this(new I18nMessage("", key, parameters),"");
	}
	
	@Override
	public Severity getSeverity() {
		return severity;
	}

	@Override
	public String getMessage() {
		return message.getMessage();
	}

	public Message withCategory(String category) {
		this.category = category;
		return this;
	}

	@Override
	public String getCategory() {
		return Objects.firstNonNull(Strings.emptyToNull(message.getCategory()), category);
	}

	@Override
	public void setBundle(ResourceBundle bundle) {
		message.setBundle(bundle);
	}

	@Override
	public String toString() {
		return toStringHelper(this).add("category", getCategory()).add("message", getMessage()).toString();
	}
}
