package br.com.caelum.vraptor.serializationx;

import static br.com.caelum.vraptor.serializationx.Options.*;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.util.test.MockResult;

public class Main {

	public static void main(String[] args)
		throws Exception {

		Result result = new MockResult();

		result.use(Json.class).from(new Object()).serialize();

		result.use(Json.class)
			.from(new Object())
			.with(include("name", "email"), exclude("birthday"))
			.serialize();
	}
}
