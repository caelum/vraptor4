package br.com.caelum.vraptor.musicjungle.acceptance.infra;

import static org.openqa.selenium.By.cssSelector;
import static org.openqa.selenium.By.name;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import br.com.caelum.vraptor.musicjungle.acceptance.pages.PageForm;


public class AcceptanceData {

	private WebDriver driver;

	public AcceptanceData(WebDriver driver) {
		this.driver = driver;
	}

	public void insertSomeMusicsToDataBase() {
		addMusic("Mozart - Symphony #40", "CLASSICAL", "Mozart");
		addMusic("Moonlight Sonata", "CLASSICAL", "Beethoven");
	}

	private void addMusic(String title, String type, String desc) {
		PageForm form = refreshPageForm();
		form.input("music.title", title);
		form.select(name("music.type"), type);
		form.input("music.description", desc);
		form.submitForm();
	}

	private PageForm refreshPageForm() {
		By cssSelector = cssSelector(".well form");
		return new PageForm(driver, cssSelector);
	}

}