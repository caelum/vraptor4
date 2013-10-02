package br.com.caelum.vraptor.serialization;

import com.google.gson.Gson;
import com.thoughtworks.xstream.XStream;

public class ExcludeAll
	implements Option {
	
	@Override
	public XStream apply(XStream serializer) {
		return serializer;
	}

	@Override
	public Gson apply(Gson serializer) {
		return serializer;
	}
}
