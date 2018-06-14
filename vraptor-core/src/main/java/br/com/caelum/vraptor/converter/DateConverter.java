/***
 * Copyright (c) 2009 Caelum - www.caelum.com.br/opensource
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package br.com.caelum.vraptor.converter;

import static com.google.common.base.Strings.isNullOrEmpty;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Locale;

import javax.annotation.Priority;
import javax.enterprise.inject.Alternative;
import javax.inject.Inject;
import javax.interceptor.Interceptor;

import br.com.caelum.vraptor.Convert;

/**
 * Locale based date converter.
 *
 * @author Guilherme Silveira
 */
@Convert(Date.class)
@Alternative
@Priority(Interceptor.Priority.LIBRARY_BEFORE)
public class DateConverter implements Converter<Date> {

	public static final String INVALID_MESSAGE_KEY = "is_not_a_valid_date";

	private final Locale locale;

	/** 
	 * @deprecated CDI eyes only
	 */
	protected DateConverter() {
		this(null);
	}

	@Inject
	public DateConverter(Locale locale) {
		this.locale = locale;
	}

	@Override
	public Date convert(String value, Class<? extends Date> type) {
		if (isNullOrEmpty(value)) {
			return null;
		}

		try {
			if(hasHourMinutesAndSeconds(value)){
				return getDateTimeMediumFormat().parse(value);
			}
			if(hasHourAndMinutes(value)){
				return getDateTimeShortFormat().parse(value);
			}
			return getDateFormat().parse(value);

		} catch (ParseException pe) {
			throw new ConversionException(new ConversionMessage(INVALID_MESSAGE_KEY, value));
		}
	}

	protected DateFormat getDateFormat() {
		return DateFormat.getDateInstance(DateFormat.SHORT, locale);
	}
	
	protected DateFormat getDateTimeMediumFormat(){
		return DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM, locale);
	}
	
	protected DateFormat getDateTimeShortFormat(){
		return DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, locale);
	}
	
	private boolean hasHourMinutesAndSeconds(String value){		
		if( isDateTime(value) && isMediumTime(value)){
			return true;
		}
		return false;
	}
	
	private boolean hasHourAndMinutes(String value){		
		if( isDateTime(value) && isShortTime(value)){
			return true;
		}
		return false;
	}
	
	private boolean isDateTime(String value){
		return value.split(" ").length > 1;
	}
	
	private boolean isMediumTime(String value){
		return value.split(" ")[1].split(":").length == 3;
	}
	
	private boolean isShortTime(String value){
		return value.split(" ")[1].split(":").length == 2;
	}
}
