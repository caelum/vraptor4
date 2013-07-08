package br.com.caelum.vraptor.musicjungle.acceptance.pages;

import org.openqa.selenium.WebDriver;

public class MusicPage {

	private WebDriver driver;

	public MusicPage(WebDriver driver) {
		this.driver = driver;
	}

	public String pageSource() {
		return driver.getPageSource();
	}

}
