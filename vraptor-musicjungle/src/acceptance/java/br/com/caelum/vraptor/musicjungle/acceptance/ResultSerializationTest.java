package br.com.caelum.vraptor.musicjungle.acceptance;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;
import static org.openqa.selenium.By.name;

import org.junit.Test;

import br.com.caelum.vraptor.musicjungle.acceptance.infra.AcceptanceTestCase;
import br.com.caelum.vraptor.musicjungle.acceptance.pages.GenericPage;

/**
 * Some tests for <code>Results</code> class, such as json(), xml() and html().
 * 
 * @author Rodrigo Turini
 */
public class ResultSerializationTest extends AcceptanceTestCase{

	private GenericPage genericPage;

	@Test
	public void shouldSerializeAMusicListAsJsonXmlAndHTTP() throws Exception {
		
		this.genericPage = accessFullUrl("/musics/list/json");
		assertThat(pageSource(), containsString(getExpectedJson()));
		
		genericPage = accessFullUrl("/musics/list/xml");
		assertThat(pageSource(), containsString(getExpectedXml()));
		
		genericPage = accessFullUrl("/musics/list/http");
		assertThat(pageSource(), containsString(getExpectedHTTP()));
		
		genericPage = accessFullUrl("/musics/list/form");
		genericPage.getForm().select(name("_format"), "xml").submit();
		
		assertThat(pageSource(), containsString(getExpectedXml()));
		genericPage = accessFullUrl("/musics/list/form");
		
		genericPage.getForm().select(name("_format"), "json").submit();
		assertThat(pageSource(), containsString(getExpectedJson()));
	}
	
	private String getExpectedJson() {
		return "{\"list\": [{\"id\": 1,\"title\": \"Mozart - Symphony " +
				"#40\",\"description\": \"A Mozart Symphony\",\"type\": " +
				"\"CLASSICAL\"},{\"id\": 2,\"title\": \"Moonlight Sonata\"," +
				"\"description\": \"Beethoven\",\"type\": \"CLASSICAL\"}]}";
	}

	private String getExpectedXml() {
		return "<list>\n"+
				"  <music>\n"+
				"    <id>1</id>\n"+
				"    <title>Mozart - Symphony #40</title>\n"+
				"    <description>A Mozart Symphony</description>\n"+
				"    <type>CLASSICAL</type>\n"+
				"  </music>\n"+
				"  <music>\n"+
				"    <id>2</id>\n"+
				"    <title>Moonlight Sonata</title>\n"+
				"    <description>Beethoven</description>\n"+
				"    <type>CLASSICAL</type>\n"+
				"  </music>\n"+
				"</list>";
	}
	
	private String getExpectedHTTP() {
		return "[Music [id=1, title=Mozart - Symphony #40, description" +
			"=A Mozart Symphony, type=CLASSICAL], Music [id=2, title=" +
			"Moonlight Sonata, description=Beethoven, type=CLASSICAL]]";
	}
	
	private String pageSource() {
		return this.genericPage.pageSource();
	}
}
