package br.com.caelum.vraptor.serialization.gson;

import javax.enterprise.inject.Vetoed;

import br.com.caelum.vraptor.serialization.Deserializee;
import br.com.caelum.vraptor.serialization.DeserializerConfig;

@Vetoed
public class WithRoot implements DeserializerConfig {

	@Override
	public void config(Deserializee deserializee) {
		deserializee.setWithoutRoot(false);
	}

}
