package br.com.caelum.vraptor.serialization.gson;

import br.com.caelum.vraptor.serialization.Serializee;

import com.google.gson.ExclusionStrategy;

public interface GsonSerializerBuilder extends GsonInterfaceBuilder {
	Serializee getSerializee();

	boolean isWithoutRoot();

	void setWithoutRoot(boolean withoutRoot);

	String getAlias();

	void setAlias(String alias);

	void indented();

	void setExclusionStrategies(ExclusionStrategy... strategies);
}
