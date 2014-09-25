package br.com.caelum.vraptor.http.iogi;

import br.com.caelum.vraptor.converter.Converter;
import br.com.caelum.vraptor.http.iogi.IogiParametersProviderTest.ABC;

public class ABCConverter implements Converter<ABC> {

	@Override
	public ABC convert(String value, Class<? extends ABC> type) {
		return new ABC();
	}

}
