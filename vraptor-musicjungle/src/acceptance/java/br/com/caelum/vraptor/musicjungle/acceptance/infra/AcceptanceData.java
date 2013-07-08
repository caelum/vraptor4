package br.com.caelum.vraptor.musicjungle.acceptance.infra;

import static org.openqa.selenium.By.cssSelector;
import static org.openqa.selenium.By.name;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import br.com.caelum.vraptor.musicjungle.acceptance.pages.PageForm;
import br.com.caelum.vraptor.musicjungle.model.Music;

/**
 * Inserts <b>manually</b> some data on {@code MusicJungle} database.
 * The point here is that doing this way, and not directly by the DB, 
 * we are ensuring {@code VRaptor} ability of inject {@code hibernate 
 * section} and populate objects coming from {@code HttpRequest} <br>
 * 
 * @author Rodrigo Turini
 */
public class AcceptanceData {

	private WebDriver driver;

	public AcceptanceData(WebDriver driver) {
		this.driver = driver;
	}

	public void addMusic(Music music) {
		PageForm form = refreshPageForm();
		form.input("music.title", music.getTitle());
		form.select(name("music.type"), music.getType().toString());
		form.input("music.description", music.getDescription());
		form.submitForm();
	}
	
	/*
	 * (non-javadoc) this refresh method is needed to prevent 
	 * {@code org.openqa.selenium.StaleElementReferenceException}
	 */
	private PageForm refreshPageForm() {
		By cssSelector = cssSelector(".well form");
		return new PageForm(driver, cssSelector);
	}

}