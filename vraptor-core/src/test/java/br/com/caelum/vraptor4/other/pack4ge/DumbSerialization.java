package br.com.caelum.vraptor4.other.pack4ge;

import br.com.caelum.vraptor4.serialization.Serialization;
import br.com.caelum.vraptor4.serialization.Serializer;

/**
 * Test Serialization's comparator
 *  
 * @author acdesouza
 */
public class DumbSerialization implements Serialization {

	public <T> Serializer from(T object) {
		return null;
	}

	public <T> Serializer from(T object, String alias) {
		return null;
	}

	public boolean accepts(String format) {
		return false;
	}

}