package br.com.caelum.vraptor.serializationx;

import br.com.caelum.vraptor.View;

public interface Json
	extends View {

	<T> Serializer from(T object);

	<T> Serializer from(T object, String alias);

}
