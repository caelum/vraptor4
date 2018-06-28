package br.com.caelum.vraptor.musicjungle.acceptance;

import static br.com.caelum.vraptor.musicjungle.enums.MusicType.ROCK;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.musicjungle.acceptance.builder.MusicBuilder;
import br.com.caelum.vraptor.musicjungle.acceptance.infra.AcceptanceTestCase;
import br.com.caelum.vraptor.musicjungle.acceptance.pages.LoginPage;
import br.com.caelum.vraptor.musicjungle.acceptance.pages.PageObject;
import br.com.caelum.vraptor.musicjungle.acceptance.pages.SearchPage;
import br.com.caelum.vraptor.musicjungle.model.Music;

/**
 * Some tests for vraptor {@code routes} of a {@code RESTFUL} resource, 
 * such as post, get, path, defaul url values, param injection and so on. 
 * 
 * @author Rodrigo Turini
 */
public class RoutesTest extends AcceptanceTestCase {

	private LoginPage loginPage;
	private Music music;
	private String userName = "vraptor";

	@Before
	public void setUp() {
		this.loginPage = loginPage();
		loginPage.loginAsUser(userName);
		this.music = new MusicBuilder(3, "Please Please Me", "Beatles", ROCK).create();
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
		assertPageContains(searchPage, musicTitle);
	}

	private void shouldAccessPostMethodWithDefaultURLValue() {
		assertPageContains(loginPage, "VRaptor Music Jungle", "Sign in");
	}

	private void assertPageContains(PageObject page, String ...args) {
		String pageSource = page.pageSource();
		for (String expectedText : args) {
			assertThat(pageSource, containsString(expectedText));
		}
	}

}
