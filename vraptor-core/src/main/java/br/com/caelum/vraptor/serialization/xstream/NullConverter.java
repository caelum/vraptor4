package br.com.caelum.vraptor.serialization.xstream;

import javax.enterprise.context.ApplicationScoped;

import com.thoughtworks.xstream.converters.SingleValueConverter;

/**
 * for DI purposes
 */
@ApplicationScoped
public class NullConverter implements SingleValueConverter {
	@Override
	public String toString(Object o) {return null;}

	@Override
	public Object fromString(String s) {return null;}

	@Override
	public boolean canConvert(Class aClass) {return false;}
}