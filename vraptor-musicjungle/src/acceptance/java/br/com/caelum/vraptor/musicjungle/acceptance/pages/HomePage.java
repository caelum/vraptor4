package br.com.caelum.vraptor.musicjungle.acceptance.pages;

import static org.openqa.selenium.By.cssSelector;
import static org.openqa.selenium.By.name;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import br.com.caelum.vraptor.musicjungle.model.Music;

public class HomePage extends PageObject{

	public HomePage(WebDriver driver) {
		super(driver);
	}

	public MusicPage clickOnFirstMusic() {
		By selector = cssSelector("table a:first-child");
		driver.findElement(selector).click();
		return new MusicPage(driver);
	}

	public SearchPage searchFor(String title) {
		By cssSelector = cssSelector("form");
		PageForm form = new PageForm(driver, cssSelector);
		form.input("music.title", title).submitForm();
		return new SearchPage(driver);
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
