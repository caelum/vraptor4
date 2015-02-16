package br.com.caelum.vraptor.musicjungle.acceptance;

import static br.com.caelum.vraptor.musicjungle.enums.MusicType.CLASSICAL;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.musicjungle.acceptance.builder.MusicBuilder;
import br.com.caelum.vraptor.musicjungle.acceptance.infra.AcceptanceTestCase;
import br.com.caelum.vraptor.musicjungle.acceptance.pages.HomePage;
import br.com.caelum.vraptor.musicjungle.acceptance.pages.ServiceResultPage;
import br.com.caelum.vraptor.musicjungle.model.Music;

/**
 * Some tests for {@code Results} class, such as json(), xml() and html().
 * 
 * @author Rodrigo Turini
 */
public class ResultSerializationTest extends AcceptanceTestCase{

	private ServiceResultPage serviceResultPage;
	private Music mozart;
	private Music beethoven;
	
	@Before
	public void setUpBeforeClass() {
		loginPage().loginAsUser("vraptor");
		HomePage page = homePage();
		this.mozart = new MusicBuilder(1, "Mozart - Symphony #40", "Mozart", CLASSICAL).create();
		this.beethoven = new MusicBuilder(2, "Moonlight Sonata", "Beethoven", CLASSICAL).create();
		page.addMusic(mozart);
		page.addMusic(beethoven);
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
	
	private void accessFormURLSelectItemAndAssertContent(String value, String v) {
		loginPage();
		accessMusicsExport().select(value);
		assertThat(pageSource(), containsString(v));
	}
	
	private String getExpectedJson() {
		return "{\"list\":[" + JSON(mozart) + "," + JSON(beethoven) + "]}";
	}

	private String JSON(Music music) {
		return String.format("{\"id\":%s,\"title\":\"%s\",\"description\":\"%s\",\"type\":\"%s\"}", 
				music.getId(), 
				music.getTitle(), 
				music.getDescription(), 
				music.getType().toString());
	}

	private String getExpectedXml() {
		return "<list>"+
				"<music>"+
				"<id>1</id>"+
				"<title>"+ mozart.getTitle() +"</title>"+
				"<description>"+ mozart.getDescription() +"</description>"+
				"<type>"+ mozart.getType().toString() +"</type>"+
				"</music>"+
				"<music>"+
				"<id>2</id>"+
				"<title>"+ beethoven.getTitle() +"</title>"+
				"<description>"+ beethoven.getDescription() +"</description>"+
				"<type>"+ beethoven.getType().toString() +"</type>"+
				"</music>"+
				"</list>";
	}
	
	private String getExpectedHTTP() {
		return "[" + mozart.toString() + ", " + beethoven.toString() + "]";
	}
	
	private String pageSource() {
		return this.serviceResultPage.pageSource();
	}
}
