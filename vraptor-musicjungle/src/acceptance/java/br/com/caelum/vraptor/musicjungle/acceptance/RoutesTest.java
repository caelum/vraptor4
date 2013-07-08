package br.com.caelum.vraptor.musicjungle.acceptance;

import static br.com.caelum.vraptor.musicjungle.enums.MusicType.CLASSICAL;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.musicjungle.acceptance.infra.AcceptanceTestCase;
import br.com.caelum.vraptor.musicjungle.acceptance.pages.LoginPage;
import br.com.caelum.vraptor.musicjungle.acceptance.pages.MusicPage;
import br.com.caelum.vraptor.musicjungle.acceptance.pages.SearchPage;
import br.com.caelum.vraptor.musicjungle.model.Music;


public class RoutesTest extends AcceptanceTestCase {

	private LoginPage loginPage;
	private Music music;
	private String userName;

	@Before
	public void setUp() {
		this.loginPage = loginPage();
		this.userName = "vraptortest";
		loginPage.loginAsUser(userName);
		this.music = new Music("Mozart - Symphony #40", "Mozart", CLASSICAL);
		acceptanceData().addMusic(music);
		loginPage.logout();
	}

	@Test
	public void shouldHandleAllRoutesTypes() throws Exception {
		// @Post testing default route /home/login 
		shouldAccessPostMethodWithDefaultURLValue();
		// @Get(/musics/search)
		shouldAccessGetMethodWithADefinedValue();
		// @Path(/musics/{music.id}) @Get()
		shouldAccessPathPlusGetMethodWithURLParameter();
	}

	private void shouldAccessPathPlusGetMethodWithURLParameter() {
		MusicPage musicPage = homePage().clickOnFirstMusic();
		String source = musicPage.pageSource();
		assertPageContains(source, music.getTitle(), "Owners:", userName);
	}

	private void shouldAccessGetMethodWithADefinedValue() {
		loginPage.loginAsUser(userName);
		String musicTitle = music.getTitle();
		SearchPage searchPage = homePage().searchFor(musicTitle);
		String source = searchPage.pageSource();
		assertPageContains(source, "Search results", musicTitle);
	}

	private void shouldAccessPostMethodWithDefaultURLValue() {
		String source = loginPage.pageSource();
		assertPageContains(source, "VRaptor Music Jungle", "Sign In");
	}

	private void assertPageContains(String source, String ...args) {
		for (String expectedText : args) {
			assertThat(source, containsString(expectedText));
		}
	}

}