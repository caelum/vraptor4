package br.com.caelum.vraptor.musicjungle.acceptance;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;
import static org.openqa.selenium.By.name;

import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.musicjungle.acceptance.infra.AcceptanceTestCase;
import br.com.caelum.vraptor.musicjungle.acceptance.pages.PageForm;
import br.com.caelum.vraptor.musicjungle.acceptance.pages.ServiceResultPage;

/**
 * Some tests for {@code Results} class, such as json(), xml() and html().
 * 
 * @author Rodrigo Turini
 */
public class ResultSerializationTest extends AcceptanceTestCase{

	private ServiceResultPage serviceResultPage;
	
	@Before
	public void setUpBeforeClass() {
		loginPage().loginAsUser("vraptortest");
		acceptanceData().insertSomeMusicsToDataBase();
		loginPage().logout();
	}

	@Test
	public void shouldSerializeAMusicListAsJsonXmlAndHTTP() throws Exception {
		accessURLAndAssertContent("/musics/list/json", getExpectedJson());
		accessURLAndAssertContent("/musics/list/xml", getExpectedXml());
		accessURLAndAssertContent("/musics/list/http", getExpectedHTTP());
		accessFormURLSelectItemAndAssertContent("xml", getExpectedXml());
		accessFormURLSelectItemAndAssertContent("json", getExpectedJson());
	}

	private void accessURLAndAssertContent(String url, String expected) {
		this.serviceResultPage = accessFullUrl(url);
		assertThat(pageSource(), containsString(expected));
	}
	
	private void accessFormURLSelectItemAndAssertContent(String item, String v) {
		loginPage();
		PageForm pageForm = accessMusicsExport().getForm();
		pageForm.select(name("_format"), item).submitForm();
		assertThat(pageSource(), containsString(v));
	}
	
	private String getExpectedJson() {
		return "{\"list\": [{\"id\": 1,\"title\": \"Mozart - Symphony " +
				"#40\",\"description\": \"Mozart\",\"type\": " +
				"\"CLASSICAL\"},{\"id\": 2,\"title\": \"Moonlight Sonata\"," +
				"\"description\": \"Beethoven\",\"type\": \"CLASSICAL\"}]}";
	}

	private String getExpectedXml() {
		return "<list>\n"+
				"  <music>\n"+
				"    <id>1</id>\n"+
				"    <title>Mozart - Symphony #40</title>\n"+
				"    <description>Mozart</description>\n"+
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
			"=Mozart, type=CLASSICAL], Music [id=2, title=" +
			"Moonlight Sonata, description=Beethoven, type=CLASSICAL]]";
	}
	
	private String pageSource() {
		return this.serviceResultPage.pageSource();
	}
}
