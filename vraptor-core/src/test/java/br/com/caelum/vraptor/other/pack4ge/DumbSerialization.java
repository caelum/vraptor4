package br.com.caelum.vraptor.other.pack4ge;

import br.com.caelum.vraptor.serialization.Serialization;
import br.com.caelum.vraptor.serialization.Serializer;

/**
 * Test Serialization's comparator
 *  
 * @author acdesouza
 */
public class DumbSerialization implements Serialization {

	@Override
	public <T> Serializer from(T object) {
		return null;
	}

	@Override
	public <T> Serializer from(T object, String alias) {
		return null;
	}

	@Override
	public boolean accepts(String format) {
		return false;
	}

}