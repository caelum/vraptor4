package br.com.caelum.vraptor.serialization;

import com.google.gson.Gson;
import com.thoughtworks.xstream.XStream;

public class Exclude
	implements Option {
	private final String[] fields;
	
	Exclude(String... fields) {
		this.fields = fields;
	}

	@Override
	public XStream apply(XStream serializer) {
		return serializer;
	}

	@Override
	public Gson apply(Gson serializer) {
		return serializer;
	}
}
