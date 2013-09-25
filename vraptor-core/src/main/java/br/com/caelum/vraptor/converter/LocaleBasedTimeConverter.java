package br.com.caelum.vraptor.converter;

import static com.google.common.base.Strings.isNullOrEmpty;

import java.sql.Time;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import br.com.caelum.vraptor.Convert;
import br.com.caelum.vraptor.Converter;

@Convert(Time.class)
@RequestScoped
public class LocaleBasedTimeConverter implements Converter<Time> {

	private Locale locale;
	private ResourceBundle bundle;

	@Deprecated // CDI eyes only
	public LocaleBasedTimeConverter() {
    }

	@Inject
	public LocaleBasedTimeConverter(Locale locale, ResourceBundle bundle) {
		this.locale = locale;
        this.bundle = bundle;
	}

	public Time convert(String value, Class<? extends Time> type) {
		if (isNullOrEmpty(value)) {
			return null;
		}

		DateFormat formatHour = DateFormat.getTimeInstance(DateFormat.SHORT, locale);
		
		try {
			if (isUncompleteTime(value)) {
				value = value + ":00";
			}
			return new Time(formatHour.parse(value).getTime());
		} catch (ParseException pe) {

			throw new ConversionException(MessageFormat.format(bundle.getString("is_not_a_valid_time"), value));
		}
	}

	private boolean isUncompleteTime(String value) {
		return Pattern.compile("[0-9]{2}\\:[0-9]{2}").matcher(value).find();
	}
}
