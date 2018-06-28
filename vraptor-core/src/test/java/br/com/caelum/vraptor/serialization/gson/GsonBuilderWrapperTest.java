package br.com.caelum.vraptor.serialization.gson;

import br.com.caelum.vraptor.events.VRaptorInitialized;
import com.google.gson.*;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.*;
import org.junit.runner.RunWith;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import java.lang.reflect.Type;

import static org.jboss.shrinkwrap.api.asset.EmptyAsset.INSTANCE;
import static org.junit.Assert.assertEquals;

@RunWith(Arquillian.class)
public class GsonBuilderWrapperTest {

	@Inject
	private GsonBuilderWrapper builder;

	private Gson gson;
	
	@Before
	public void init(){
		gson = builder.create();
	}

	@Deployment
	public static WebArchive createDeployment() {
		return ShrinkWrap
			.create(WebArchive.class)
				.addPackages(true, "br.com.caelum.vraptor")
			.addAsManifestResource(INSTANCE, "beans.xml");
	}

	@Test
	public void test() {
		String json = gson.toJson(new Bean());
		assertEquals("{\"test123\":{}}", json);
	}
}

class Bean{
	
}