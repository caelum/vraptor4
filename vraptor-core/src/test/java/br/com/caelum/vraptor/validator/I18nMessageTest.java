package br.com.caelum.vraptor.validator;

import static br.com.caelum.vraptor.validator.Severity.INFO;
import static br.com.caelum.vraptor.validator.Severity.WARN;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class I18nMessageTest {

	@Test
	public void shouldBeEqualAccordingToMessageCategoryParamsAndSeverity() {

		Message m1 = new I18nMessage(new I18nParam("foo"), "equal", INFO, "param");
		Message m2 = new I18nMessage(new I18nParam("foo"), "equal", INFO, "param");
		assertThat("should be equals, since has the same params", m1, equalTo(m2));
		
		Message m3 = new I18nMessage(new I18nParam("bar"), "equal", INFO, "param");
		assertThat("shouldn't be equals, i18nParam is different", m2, not(equalTo(m3)));
		
		Message m4 = new I18nMessage(new I18nParam("bar"), "not.equal", INFO, "param");
		assertThat("shouldn't be equals, category is different", m3, not(equalTo(m4)));
		
		Message m5 = new I18nMessage(new I18nParam("bar"), "not.equal", INFO, "other");
		assertThat("shouldn't be equals, message is different", m4, not(equalTo(m5)));
		
		Message m6 = new I18nMessage(new I18nParam("bar"), "not.equal", WARN, "other");
		assertThat("shouldn't be equals, severity is different", m5, not(equalTo(m6)));
	}
}
