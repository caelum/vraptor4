package br.com.caelum.vraptor.serializationx;

import com.google.gson.Gson;
import com.thoughtworks.xstream.XStream;

public interface Option {

	XStream apply(XStream serializer);

	Gson apply(Gson serializer);
}