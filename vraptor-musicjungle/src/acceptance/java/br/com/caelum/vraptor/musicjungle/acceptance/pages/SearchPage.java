package br.com.caelum.vraptor.musicjungle.acceptance.pages;

import org.openqa.selenium.WebDriver;

public class SearchPage {

	private WebDriver driver;

	public SearchPage(WebDriver driver) {
		this.driver = driver;
	}
	
	public String pageSource() {
		return driver.getPageSource();
	}

}
