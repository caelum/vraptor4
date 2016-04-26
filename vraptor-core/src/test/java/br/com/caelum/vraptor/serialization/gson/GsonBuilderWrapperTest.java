package br.com.caelum.vraptor.serialization.gson;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Type;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import br.com.caelum.vraptor.WeldJunitRunner;

@RunWith(WeldJunitRunner.class)
public class GsonBuilderWrapperTest {
	private @Inject GsonBuilderWrapper builder;
	private Gson gson;
	
	@Before
	public void init(){
		gson = builder.create();
	}
	
	@Test
	public void test() {
		String json = gson.toJson(new Bean());
		assertEquals("{\"test123\":{}}", json);
	}
}

class Bean{
	
}


@RegisterStrategy(RegisterType.SINGLE)
@RequestScoped
class BeanSerializer implements JsonSerializer<Bean> {
	private static final JsonObject element = new JsonObject();
	static{
		element.add("test123", new JsonObject());
	}
	
	@Override
	public JsonElement serialize(Bean src, Type typeOfSrc, JsonSerializationContext context) {
		return element;
	}
}