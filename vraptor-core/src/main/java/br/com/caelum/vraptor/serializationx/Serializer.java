package br.com.caelum.vraptor.serializationx;

public interface Serializer {
	
	Serializer with(Option... optins);
	
	void serialize();

}
