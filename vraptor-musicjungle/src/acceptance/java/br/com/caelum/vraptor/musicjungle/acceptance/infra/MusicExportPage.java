package br.com.caelum.vraptor.musicjungle.acceptance.infra;

import static org.openqa.selenium.By.tagName;

import org.openqa.selenium.WebDriver;

import br.com.caelum.vraptor.musicjungle.acceptance.pages.PageForm;

public class MusicExportPage {

	private WebDriver driver;

	public MusicExportPage(WebDriver driver) {
		this.driver = driver;
	}

	public PageForm getForm() {
		return new PageForm(driver, tagName("form"));
	}

}
