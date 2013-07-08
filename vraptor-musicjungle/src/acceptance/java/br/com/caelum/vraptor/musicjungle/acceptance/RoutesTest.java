package br.com.caelum.vraptor.musicjungle.acceptance;

import static br.com.caelum.vraptor.musicjungle.enums.MusicType.CLASSICAL;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.musicjungle.acceptance.infra.AcceptanceTestCase;
import br.com.caelum.vraptor.musicjungle.acceptance.pages.LoginPage;
import br.com.caelum.vraptor.musicjungle.acceptance.pages.PageObject;
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
		homePage().addMusic(music);
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
		PageObject musicPage = homePage().clickOnFirstMusic();
		assertPageContains(musicPage, music.getTitle(), "Owners:", userName);
	}

	private void shouldAccessGetMethodWithADefinedValue() {
		loginPage.loginAsUser(userName);
		String musicTitle = music.getTitle();
		SearchPage searchPage = homePage().searchFor(musicTitle);
		assertPageContains(searchPage, "Search results", musicTitle);
	}

	private void shouldAccessPostMethodWithDefaultURLValue() {
		assertPageContains(loginPage, "VRaptor Music Jungle", "Sign In");
	}

	private void assertPageContains(PageObject page, String ...args) {
		String pageSource = page.pageSource();
		for (String expectedText : args) {
			assertThat(pageSource, containsString(expectedText));
		}
	}

}