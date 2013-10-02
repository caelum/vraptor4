package br.com.caelum.vraptor.serialization;

import com.google.gson.Gson;
import com.thoughtworks.xstream.XStream;

public class Include
	implements Option {
	private final String[] fields;

	Include(String... fields) {
		this.fields = fields;
	}

	@Override
	public Gson apply(Gson serializer) {
		return serializer;
	}

	@Override
	public XStream apply(XStream serializer) {
		return serializer;
	}
}