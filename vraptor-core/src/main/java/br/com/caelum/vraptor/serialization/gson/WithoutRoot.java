package br.com.caelum.vraptor.serialization.gson;

import javax.enterprise.context.Dependent;

import br.com.caelum.vraptor.Consumes;
import br.com.caelum.vraptor.serialization.Deserializee;
import br.com.caelum.vraptor.serialization.DeserializerConfig;

/**
 * A {@link Consumes} option to force deserialization without root element.
 * 
 * @author Renan Montenegro
 * @since 4.1
 */
@Dependent
public class WithoutRoot implements DeserializerConfig {

	@Override
	public void config(Deserializee deserializee) {
		deserializee.setWithoutRoot(true);
	}

}
